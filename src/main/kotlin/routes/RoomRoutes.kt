package edu.mci.routes

import edu.mci.model.api.response.EquipmentResponse
import edu.mci.model.api.response.RoomWithBookingsResponse
import edu.mci.service.RoomService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate

fun Route.roomRoutes(roomService: RoomService) {

    route("/rooms") {
        /**
         * Get all rooms. (With bookings for date)
         *
         * @tag Rooms
         * @query capacity [Int] Filter rooms by minimum capacity
         * @query equipment [String] Comma-separated list of required equipment types. Values: BEAMER, HDMI_CABLE, WHITEBOARD, DISPLAY
         * @query buildingId [Int] Filter rooms by building ID
         * @query date [String] Filter rooms and include bookings for this specific date. Format: 2026-01-14
         * @response 200 application/json [RoomWithBookingsResponse] List of rooms matching the filters. Room status values: FREE, RESERVED, OCCUPIED.
         * @response 400 text/plain Invalid capacity format
         */
        get {
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

        /**
         * Get all available equipment for a specific building.
         *
         * @tag Rooms
         * @query buildingId [Int] The ID of the building to fetch equipment for
         * @response 200 application/json [EquipmentResponse] List of equipment available in the building (Array). Equipment types: BEAMER, HDMI_CABLE, WHITEBOARD, DISPLAY.
         * @response 400 text/plain Missing or invalid buildingId
         */
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
