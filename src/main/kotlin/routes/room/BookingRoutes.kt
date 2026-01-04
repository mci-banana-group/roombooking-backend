package edu.mci.routes.room

import edu.mci.model.dto.CheckInDto
import edu.mci.model.dto.CreateBookingDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bookingRoutes() {
    route("/bookings") {
        get("/me") {
            // TODO only get bookings from authenticated user

            call.respondText(text = "Your Bookings: not implemented yet.", status = HttpStatusCode.OK)
        }

        post {


            call.respondText(
                text = "Booking Created Successfully: not implemented yet.",
                status = HttpStatusCode.Created
            )
        }

        put("/{bookingId}") {
            val bookingId = runCatching { call.pathParameters["bookingId"] }.getOrNull()
            val editedBooking = runCatching { call.receive<CreateBookingDto>() }.getOrNull()

            if (editedBooking == null) {
                call.respondText(text = "Malformed Body", status = HttpStatusCode.BadRequest)
            } else {
                // TODO: fetch existing booking from db to compare userId (could also expose the userId to clients and back, to avoid db request)
                call.respondText(text = "Booking Updated Successfully.", status = HttpStatusCode.Accepted)
            }
        }

        delete("/{bookingId}") {
            val bookingId = runCatching { call.pathParameters["bookingId"] }.getOrNull()

            if (bookingId == null) {
                call.respondText(text = "Malformed Request", status = HttpStatusCode.BadRequest)
            } else {
                // TODO: fetch existing booking from db to compare userId and delete
                call.respond(Unit)
            }
        }

        post("/checkin") {
            val checkInRequest = runCatching { call.receive<CheckInDto>() }.getOrNull()

            if (checkInRequest == null) {
                call.respondText(text = "Bad Request", status = HttpStatusCode.BadRequest)
            } else {
                // TODO: check userID matching with user from booking etc.

                call.respond(Unit)
            }
        }
    }
}