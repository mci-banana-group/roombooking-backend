package edu.mci

import edu.mci.routes.room.bookingRoutes
import edu.mci.routes.room.buildingRoutes
import edu.mci.routes.room.roomRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
}


private fun Application.configureRouting() {
    routing {
        roomRoutes()
        bookingRoutes()
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


