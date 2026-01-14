package edu.mci

import edu.mci.repository.BookingRepositoryImpl
import edu.mci.repository.RoomRepositoryImpl
import edu.mci.repository.UserRepositoryImpl
import edu.mci.routes.room.bookingRoutes
import edu.mci.routes.room.buildingRoutes
import edu.mci.routes.room.roomRoutes
import edu.mci.service.BookingService
import io.ktor.serialization.kotlinx.json.*
import edu.mci.plugins.configureDatabases
import edu.mci.plugins.seedData
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.swaggerUI
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
    val bookingService = BookingService(bookingRepository, roomRepository, userRepository)

    configureRouting(bookingService)
}

private fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}


private fun Application.configureRouting(bookingService: BookingService) {
    routing {
        swaggerUI(path = "/swagger", swaggerFile = "openapi/open-api.json")
        roomRoutes()
        bookingRoutes(bookingService)
        buildingRoutes()
    }
}

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }
}


