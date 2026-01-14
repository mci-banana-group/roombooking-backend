package edu.mci.routes.room

import edu.mci.service.RoomService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate

fun Route.roomRoutes(roomService: RoomService) {

    route("/rooms") {
        get {
            // returns all rooms with matching filters and bookings for given date. Returns RoomWithBookingsResponse
            val queryParameters = call.request.queryParameters
            val capacity = runCatching {
                queryParameters["capacity"]?.toInt()
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "capacity has to be an integer")
                return@get
            }.getOrThrow()

            val equipment = queryParameters["equipment"]?.split(",") ?: emptyList()
            val buildingId = queryParameters["buildingId"]?.toIntOrNull()
            val date = queryParameters["date"]?.let { LocalDate.parse(it) }

            val rooms = roomService.getAllRooms(
                capacity = capacity,
                buildingId = buildingId,
                date = date,
                requiredEquipment = equipment
            )

            call.respond(rooms)
        }

        get("/equipment") {
            val buildingId = call.request.queryParameters["buildingId"]?.toIntOrNull()
            if (buildingId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid buildingId")
                return@get
            }

            val equipment = roomService.getAllEquipmentForBuilding(buildingId)
            call.respond(equipment)
        }
    }
}
