package edu.mci.routes.room

import edu.mci.service.BuildingService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.buildingRoutes(buildingService: BuildingService) {
    get("/buildings") {
        call.respond(message = buildingService.getALlBuildings(), status = HttpStatusCode.OK)
    }
}