package edu.mci.routes.room

import edu.mci.model.api.response.BuildingResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.buildingRoutes() {
    get("/buildings") {

        call.respond(message = listOf<BuildingResponse>(), status = HttpStatusCode.OK)
    }
}