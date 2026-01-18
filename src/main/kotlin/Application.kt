package edu.mci

import edu.mci.plugins.configureDatabases
import edu.mci.plugins.seedData
import edu.mci.repository.*
import edu.mci.routes.bookingRoutes
import edu.mci.routes.buildingRoutes
import edu.mci.routes.roomRoutes
import edu.mci.service.BookingScheduler
import edu.mci.service.BookingService
import edu.mci.service.BuildingService
import edu.mci.service.RoomService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureDatabases()
    seedData()
    configureSerialization()

    // Service initialization
    val bookingRepository = BookingRepositoryImpl()
    val roomRepository = RoomRepositoryImpl()
    val userRepository = UserRepositoryImpl()
    val equipmentRepository = EquipmentRepositoryImpl()
    val buildingRepository = BuildingRepositoryImpl()
    val bookingService = BookingService(bookingRepository, roomRepository, userRepository)
    val roomService = RoomService(roomRepository, bookingRepository, equipmentRepository)
    val buildingService = BuildingService(buildingRepository)
    
    val bookingScheduler = BookingScheduler(bookingRepository)
    bookingScheduler.start()

    configureRouting(bookingService, roomService, buildingService)
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
    buildingService: BuildingService
) {
    routing {
        swaggerUI(path = "/swagger", swaggerFile = "openapi/open-api.json")
        roomRoutes(roomService)
        bookingRoutes(bookingService)
        buildingRoutes(buildingService)
    }
}

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
}


