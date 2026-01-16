package edu.mci.routes

import edu.mci.model.api.response.BuildingResponse
import edu.mci.service.BuildingService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.buildingRoutes(buildingService: BuildingService) {
    /**
     * Get all buildings.
     *
     * @tag Buildings
     * @response 200 application/json [BuildingResponse] List of all available buildings
     */
get("/buildings") {
        call.respond(message = buildingService.getAllBuildings(), status = HttpStatusCode.OK)
    }
}