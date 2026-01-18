package edu.mci.routes

import edu.mci.model.api.request.CheckInRequest
import edu.mci.model.api.request.CreateBookingRequest
import edu.mci.model.api.response.BookingResponse
import edu.mci.service.BookingService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bookingRoutes(bookingService: BookingService) {
    route("/bookings") {
        val mockUserId = 2 // Using userId 2 (lecturer from seed) as mock authenticated user

        /**
         * Get all bookings for the currently authenticated user.
         *
         * @tag Bookings
         * @description Timestamps in ISO-8601 UTC Strings: 2026-01-14T12:34:56Z. - Status values: RESERVED, CANCELLED, CHECKED_IN, NO_SHOW. - User roles: STUDENT, STAFF, LECTURER.
         * @response 200 application/json [BookingResponse] List of bookings for the user.
         * @response 500 text/plain Internal server error
         */
        get("/me") {
            runCatching {
                bookingService.getBookingsForUser(mockUserId)
            }.onSuccess { bookings ->
                call.respond(bookings)
            }.onFailure { e ->
                call.respondText(e.message ?: "Internal Server Error", status = HttpStatusCode.InternalServerError)
            }
        }

        /**
         * Create a new booking.
         *
         * @tag Bookings
         * @description Timestamps in ISO-8601 UTC Strings: 2026-01-14T12:34:56Z. - Status values: RESERVED, CANCELLED, CHECKED_IN, NO_SHOW. - User roles: STUDENT, STAFF, LECTURER.
         * @body application/json [CreateBookingRequest] Details of the booking to create.
         * @response 201 application/json [BookingResponse] Booking created successfully.
         * @response 400 text/plain Invalid request data or room/user not found
         * @response 500 text/plain Internal server error
         */
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

                    is IllegalStateException -> call.respondText(
                        e.message ?: "Conflict",
                        status = HttpStatusCode.Conflict
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        /**
         * Update an existing booking.
         *
         * @tag Bookings
         * @description Timestamps in ISO-8601 UTC Strings: 2026-01-14T12:34:56Z. - Status values: RESERVED, CANCELLED, CHECKED_IN, NO_SHOW. - User roles: STUDENT, STAFF, LECTURER.
         * @path bookingId [Int] The ID of the booking to update
         * @body application/json [CreateBookingRequest] Updated booking details.
         * @response 202 application/json [BookingResponse] Booking updated successfully.
         * @response 400 text/plain Invalid booking ID
         * @response 403 text/plain Unauthorized to update this booking
         * @response 404 text/plain Booking or room not found
         * @response 500 text/plain Internal server error
         */
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

        /**
         * Delete a booking.
         *
         * @tag Bookings
         * @path bookingId [Int] The ID of the booking to delete
         * @response 204 Booking deleted successfully
         * @response 400 text/plain Invalid booking ID
         * @response 403 text/plain Unauthorized to delete this booking
         * @response 404 text/plain Booking not found
         * @response 500 text/plain Internal server error
         */
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

        /**
         * Check in to a booking using a confirmation code.
         *
         * @tag Bookings
         * @body application/json [CheckInRequest] Booking ID and confirmation code
         * @response 200 Check-in successful
         * @response 403 text/plain Unauthorized to check in for this booking
         * @response 404 text/plain Booking not found or confirmation code mismatch
         * @response 500 text/plain Internal server error
         */
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