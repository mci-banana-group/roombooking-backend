package edu.mci.routes.room

import edu.mci.model.api.response.EquipmentResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.roomRoutes() {


    route("/rooms") {
        get {
            // returns all rooms with matching filters and bookings for given date. Returns RoomWithBookingsDTO
            val queryParameters = call.request.queryParameters
            val capacity = runCatching { queryParameters["capacity"] }.getOrNull()
            val equipment = runCatching { queryParameters["equipment"] }.getOrNull()
            val buildingId = runCatching { queryParameters["buildingId"] }.getOrNull()
            val dateForBookings = runCatching { queryParameters["date"] }.getOrNull() // do not fetch bookings if null

            // TODO: only return bookings where booking status == RESERVED or == CONFIRMED

            call.respondText(
                text = "Rooms with capacity: $capacity \n equipment: $equipment \n buildingId: $buildingId",
                status = HttpStatusCode.OK
            )
        }

        get("/equipment") {
            call.respond(
                status = HttpStatusCode.OK,
                message = listOf<EquipmentResponse>(),
            )
        }
    }
}
