package edu.mci.routes.room

import edu.mci.model.dto.BuildingDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.buildingRoutes() {
    get("/buildings") {

        call.respond(message = listOf<BuildingDto>(), status = HttpStatusCode.OK)
    }
}