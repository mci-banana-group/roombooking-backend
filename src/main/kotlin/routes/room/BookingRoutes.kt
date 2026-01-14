package edu.mci.routes.room

import edu.mci.model.api.request.CheckInRequest
import edu.mci.model.api.request.CreateBookingRequest
import edu.mci.service.BookingService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bookingRoutes(bookingService: BookingService) {
    route("/bookings") {
        val mockUserId = 2 // Using userId 2 (lecturer from seed) as mock authenticated user

        get("/me") {
            runCatching {
                bookingService.getBookingsForUser(mockUserId)
            }.onSuccess { bookings ->
                call.respond(bookings)
            }.onFailure { e ->
                call.respondText(e.message ?: "Internal Server Error", status = HttpStatusCode.InternalServerError)
            }
        }

        post {
            runCatching {
                val request = call.receive<CreateBookingRequest>()
                bookingService.createBooking(mockUserId, request)
            }.onSuccess { booking ->
                call.respond(HttpStatusCode.Created, booking)
            }.onFailure { e ->
                when (e) {
                    is IllegalArgumentException -> call.respondText(
                        e.message ?: "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        put("/{bookingId}") {
            val bookingId = call.parameters["bookingId"]?.toIntOrNull()
            if (bookingId == null) {
                call.respondText(text = "Invalid Booking ID", status = HttpStatusCode.BadRequest)
                return@put
            }

            runCatching {
                val request = call.receive<CreateBookingRequest>()
                bookingService.updateBooking(mockUserId, bookingId, request)
            }.onSuccess { updatedBooking ->
                call.respond(HttpStatusCode.Accepted, updatedBooking)
            }.onFailure { e ->
                when (e) {
                    is IllegalArgumentException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is IllegalAccessException -> call.respondText(
                        e.message ?: "Forbidden",
                        status = HttpStatusCode.Forbidden
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        delete("/{bookingId}") {
            val bookingId = call.parameters["bookingId"]?.toIntOrNull()
            if (bookingId == null) {
                call.respondText(text = "Invalid Booking ID", status = HttpStatusCode.BadRequest)
                return@delete
            }

            runCatching {
                bookingService.deleteBooking(mockUserId, bookingId)
            }.onSuccess {
                call.respond(HttpStatusCode.NoContent)
            }.onFailure { e ->
                when (e) {
                    is IllegalArgumentException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is IllegalAccessException -> call.respondText(
                        e.message ?: "Forbidden",
                        status = HttpStatusCode.Forbidden
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        post("/checkin") {
            runCatching {
                val checkInRequest = call.receive<CheckInRequest>()
                bookingService.checkIn(mockUserId, checkInRequest)
            }.onSuccess {
                call.respond(HttpStatusCode.OK)
            }.onFailure { e ->
                when (e) {
                    is IllegalArgumentException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is IllegalAccessException -> call.respondText(
                        e.message ?: "Forbidden",
                        status = HttpStatusCode.Forbidden
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
    }
}