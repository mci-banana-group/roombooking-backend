package edu.mci

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import edu.mci.plugins.configureDatabases
import edu.mci.plugins.configureHTTP
import edu.mci.plugins.seedData
import edu.mci.repository.*
import edu.mci.routes.adminRoutes
import edu.mci.routes.authRoutes
import edu.mci.routes.bookingRoutes
import edu.mci.routes.buildingRoutes
import edu.mci.routes.roomRoutes
import edu.mci.service.BookingScheduler
import edu.mci.service.BookingService
import edu.mci.service.BuildingService
import edu.mci.service.RoomService
import edu.mci.service.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureHTTP()
    configureDatabases()
    seedData()
    configureSerialization()

    // Service initialization
    val bookingRepository = BookingRepositoryImpl()
    val roomRepository = RoomRepositoryImpl()
    val userRepository = UserRepositoryImpl()
    val equipmentRepository = EquipmentRepositoryImpl()
    val buildingRepository = BuildingRepositoryImpl()
    val searchedItemRepository = SearchedItemRepositoryImpl()

    val passwordService = BCryptPasswordService()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    val authService = AuthService(userRepository, passwordService, jwtSecret, jwtIssuer, jwtAudience)

    configureAuth(jwtSecret, jwtIssuer, jwtAudience, jwtRealm)

    val bookingService = BookingService(bookingRepository, roomRepository, userRepository)
    val roomService = RoomService(
        roomRepository,
        bookingRepository,
        equipmentRepository,
        searchedItemRepository,
        buildingRepository
    )
    val buildingService = BuildingService(buildingRepository, roomRepository)
    val adminService = AdminService(bookingRepository, searchedItemRepository)
    val mqttBrokerUrl = environment.config.property("mqtt.brokerUrl").getString()
    val mqttClientId = environment.config.property("mqtt.clientId").getString()
    val mqttService = MqttService(mqttBrokerUrl, mqttClientId)

    val bookingScheduler = BookingScheduler(bookingRepository, mqttService)
    bookingScheduler.start()

    configureRouting(bookingService, roomService, buildingService, authService, adminService)
}

private fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}


private fun Application.configureRouting(
    bookingService: BookingService,
    roomService: RoomService,
    buildingService: BuildingService,
    authService: AuthService,
    adminService: AdminService
) {
    routing {
        swaggerUI(path = "/swagger", swaggerFile = "openapi/open-api.json")
        authRoutes(authService)
        authenticate("auth-jwt") {
            roomRoutes(roomService)
            bookingRoutes(bookingService)
            buildingRoutes(buildingService)
            adminRoutes(adminService, bookingService, roomService, buildingService)
        }
    }
}

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
}

private fun Application.configureAuth(
    jwtSecret: String,
    jwtIssuer: String,
    jwtAudience: String,
    jwtRealm: String
) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
