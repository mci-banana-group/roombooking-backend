package edu.mci

import edu.mci.routes.room.bookingRoutes
import edu.mci.routes.room.buildingRoutes
import edu.mci.routes.room.roomRoutes
import io.ktor.serialization.kotlinx.json.*
import edu.mci.plugins.configureDatabases
import edu.mci.plugins.seedData
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    seedData()
    configureRouting()
    configureSerialization()
}


private fun Application.configureRouting() {
    routing {
        swaggerUI(path = "/swagger", swaggerFile = "openapi/open-api.json")
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


