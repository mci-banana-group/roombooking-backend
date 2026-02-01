package edu.mci.routes

import edu.mci.model.api.request.CreateBuildingRequest
import edu.mci.model.api.request.CreateRoomRequest
import edu.mci.model.api.request.CreateUserRequest
import edu.mci.model.api.request.UpdateBuildingRequest
import edu.mci.model.api.request.UpdateRoomRequest
import edu.mci.model.api.request.UpdateUserRoleRequest
import edu.mci.model.api.response.AdminDashboardResponse
import edu.mci.model.api.response.AdminRoomResponse
import edu.mci.service.AdminService
import edu.mci.service.AdminUserService
import edu.mci.service.BookingService
import edu.mci.service.BuildingConflictException
import edu.mci.service.BuildingDeletionBlockedException
import edu.mci.service.BuildingNotFoundException
import edu.mci.service.BuildingService
import edu.mci.service.BuildingValidationException
import edu.mci.service.RoomDeletionBlockedException
import edu.mci.service.RoomNotFoundException
import edu.mci.service.RoomService
import edu.mci.service.RoomValidationException
import edu.mci.service.UserConflictException
import edu.mci.service.UserDeletionBlockedException
import edu.mci.service.UserNotFoundException
import edu.mci.service.UserValidationException
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime

fun Route.adminRoutes(
    adminService: AdminService,
    adminUserService: AdminUserService,
    bookingService: BookingService,
    roomService: RoomService,
    buildingService: BuildingService
) {
    route("/admin") {
        authenticateAdmin {
            /**
             * Get dashboard statistics for admins.
             *
             * @tag Admin
             * @query start [String] Start date-time (ISO 8601). Format: 2026-01-01T00:00:00
             * @query end [String] End date-time (ISO 8601). Format: 2026-01-31T23:59:59
             * @query equipmentLimit [Int] Max number of most searched items to return (default 10, max 100).
             * @query roomLimit [Int] Max number of most used rooms to return (default 10, max 100).
             * @response 200 application/json [AdminDashboardResponse] Statistics for the dashboard.
             * @response 401 text/plain Unauthorized
             * @response 403 text/plain Forbidden (not an admin)
             * @response 400 text/plain Invalid date format or limits
             */
            get("/stats") {
                val queryParams = call.request.queryParameters
                val startStr = queryParams["start"]
                val endStr = queryParams["end"]
                val equipmentLimitStr = queryParams["equipmentLimit"]
                val roomLimitStr = queryParams["roomLimit"]

                if (startStr == null || endStr == null) {
                    call.respond(HttpStatusCode.BadRequest, "Start and end date-time are required")
                    return@get
                }

                val start = runCatching { LocalDateTime.parse(startStr) }.getOrNull()
                val end = runCatching { LocalDateTime.parse(endStr) }.getOrNull()

                if (start == null || end == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid date-time format. Use ISO 8601 (e.g., 2026-01-01T00:00:00)"
                    )
                    return@get
                }

                val equipmentLimit = equipmentLimitStr?.toIntOrNull() ?: 10
                if (equipmentLimit <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "equipmentLimit must be a positive integer")
                    return@get
                }
                val finalEquipmentLimit = equipmentLimit.coerceAtMost(100)

                val roomLimit = roomLimitStr?.toIntOrNull() ?: 10
                if (roomLimit <= 0) {
                    call.respond(HttpStatusCode.BadRequest, "roomLimit must be a positive integer")
                    return@get
                }
                val finalRoomLimit = roomLimit.coerceAtMost(100)

                val stats = adminService.getDashboardStats(start, end, finalEquipmentLimit, finalRoomLimit)
                call.respond(stats)
        }

        /**
         * Create a user. Only accessible by admins.
         *
         * @tag Admin
         * @body application/json [CreateUserRequest] User details.
         * @response 201 application/json [UserResponse] User created successfully.
         * @response 400 text/plain Invalid request data
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 409 text/plain User already exists
         * @response 500 text/plain Internal server error
         */
        post("/users") {
            runCatching {
                val request = call.receive<CreateUserRequest>()
                adminUserService.createUser(request)
            }.onSuccess { user ->
                call.respond(HttpStatusCode.Created, user)
            }.onFailure { e ->
                when (e) {
                    is UserValidationException -> call.respondText(
                        e.message ?: "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )

                    is UserConflictException -> call.respondText(
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
         * Get all users. Only accessible by admins.
         *
         * @tag Admin
         * @response 200 application/json [UserResponse] List of users.
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 500 text/plain Internal server error
         */
        get("/users") {
            runCatching {
                adminUserService.getAllUsers()
            }.onSuccess { users ->
                call.respond(HttpStatusCode.OK, users)
            }.onFailure { e ->
                call.respondText(
                    e.message ?: "Internal Server Error",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }

        /**
         * Update a user's role. Only accessible by admins.
         *
         * @tag Admin
         * @path userId [Int] The ID of the user to update.
         * @body application/json [UpdateUserRoleRequest] Role and/or permission update request.
         * @response 200 application/json [UserResponse] User updated successfully.
         * @response 400 text/plain Invalid request data
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain User not found
         * @response 500 text/plain Internal server error
         */
        patch("/users/{userId}/role") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respondText(text = "Invalid User ID", status = HttpStatusCode.BadRequest)
                return@patch
            }

            runCatching {
                val request = call.receive<UpdateUserRoleRequest>()
                adminUserService.updateRole(userId, request)
            }.onSuccess { user ->
                call.respond(HttpStatusCode.OK, user)
            }.onFailure { e ->
                when (e) {
                    is UserNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is UserValidationException -> call.respondText(
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

        /**
         * Delete a user. Only accessible by admins.
         *
         * @tag Admin
         * @path userId [Int] The ID of the user to delete.
         * @response 204 User deleted successfully
         * @response 400 text/plain Invalid user ID
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain User not found
         * @response 409 application/json [UserDeletionConflictResponse] User deletion blocked by active bookings
         * @response 500 text/plain Internal server error
         */
        delete("/users/{userId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
            if (userId == null) {
                call.respondText(text = "Invalid User ID", status = HttpStatusCode.BadRequest)
                return@delete
            }

            if (call.getUserId() == userId) {
                call.respondText(
                    text = "Admins cannot delete their own account",
                    status = HttpStatusCode.Forbidden
                )
                return@delete
            }

            runCatching {
                adminUserService.deleteUser(userId)
            }.onSuccess {
                call.respond(HttpStatusCode.NoContent)
            }.onFailure { e ->
                when (e) {
                    is UserNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is UserDeletionBlockedException -> call.respond(
                        HttpStatusCode.Conflict,
                        e.conflict
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        /**
         * Create a building. Only accessible by admins.
         *
         * @tag Admin
         * @body application/json [CreateBuildingRequest] Building details.
         * @response 201 application/json [BuildingResponse] Building created successfully.
         * @response 400 text/plain Invalid request data
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 409 text/plain Building already exists
         * @response 500 text/plain Internal server error
         */
        post("/buildings") {
            runCatching {
                val request = call.receive<CreateBuildingRequest>()
                buildingService.createBuilding(request.name, request.address)
            }.onSuccess { building ->
                call.respond(HttpStatusCode.Created, building)
            }.onFailure { e ->
                when (e) {
                    is BuildingValidationException -> call.respondText(
                        e.message ?: "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )

                    is BuildingConflictException -> call.respondText(
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
         * Update a building. Only accessible by admins.
         *
         * @tag Admin
         * @path buildingId [Int] The ID of the building to update.
         * @body application/json [UpdateBuildingRequest] Updated building details.
         * @response 202 application/json [BuildingResponse] Building updated successfully.
         * @response 400 text/plain Invalid request data
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain Building not found
         * @response 409 text/plain Building already exists
         * @response 500 text/plain Internal server error
         */
        put("/buildings/{buildingId}") {
            val buildingId = call.parameters["buildingId"]?.toIntOrNull()
            if (buildingId == null) {
                call.respondText(text = "Invalid Building ID", status = HttpStatusCode.BadRequest)
                return@put
            }

            runCatching {
                val request = call.receive<UpdateBuildingRequest>()
                buildingService.updateBuilding(buildingId, request.name, request.address)
            }.onSuccess { building ->
                call.respond(HttpStatusCode.Accepted, building)
            }.onFailure { e ->
                when (e) {
                    is BuildingValidationException -> call.respondText(
                        e.message ?: "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )

                    is BuildingNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is BuildingConflictException -> call.respondText(
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
         * Delete a building. Only accessible by admins.
         *
         * @tag Admin
         * @path buildingId [Int] The ID of the building to delete.
         * @response 204 Building deleted successfully
         * @response 400 text/plain Invalid building ID
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain Building not found
         * @response 409 application/json [BuildingDeletionConflictResponse] Building deletion blocked by rooms
         * @response 500 text/plain Internal server error
         */
        delete("/buildings/{buildingId}") {
            val buildingId = call.parameters["buildingId"]?.toIntOrNull()
            if (buildingId == null) {
                call.respondText(text = "Invalid Building ID", status = HttpStatusCode.BadRequest)
                return@delete
            }

            runCatching {
                buildingService.deleteBuilding(buildingId)
            }.onSuccess {
                call.respond(HttpStatusCode.NoContent)
            }.onFailure { e ->
                when (e) {
                    is BuildingNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is BuildingDeletionBlockedException -> call.respond(
                        HttpStatusCode.Conflict,
                        e.conflict
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        /**
         * Cancel a booking. Only accessible by admins.
         *
         * @tag Admin
         * @path bookingId [Int] The ID of the booking to cancel
         * @response 204 Booking cancelled successfully
         * @response 400 text/plain Invalid booking ID
         * @response 403 text/plain Only admins can cancel bookings
         * @response 404 text/plain Booking not found
         * @response 409 text/plain Booking cannot be cancelled
         * @response 500 text/plain Internal server error
         */
        patch("/bookings/{bookingId}/cancel") {
            val bookingId = call.parameters["bookingId"]?.toIntOrNull()
            if (bookingId == null) {
                call.respondText(text = "Invalid Booking ID", status = HttpStatusCode.BadRequest)
                return@patch
            }

            runCatching {
                bookingService.adminCancelBooking(bookingId)
            }.onSuccess {
                call.respond(HttpStatusCode.NoContent)
            }.onFailure { e ->
                when (e) {
                    is IllegalArgumentException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
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
         * Get all rooms with confirmation codes. Only accessible by admins.
         *
         * @tag Admin
         * @query capacity [Int] Filter rooms by minimum capacity
         * @query equipment [String] Comma-separated list of required equipment types. Values: BEAMER, HDMI_CABLE, WHITEBOARD, DISPLAY
         * @query buildingId [Int] Filter rooms by building ID
         * @response 200 application/json [AdminRoomResponse] List of rooms matching the filters.
         * @response 400 text/plain Invalid capacity format
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         */
        get("/rooms") {
            val queryParameters = call.request.queryParameters
            val capacity = runCatching {
                queryParameters["capacity"]?.toInt()
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "capacity has to be an integer")
                return@get
            }.getOrThrow()

            val equipment = queryParameters["equipment"]?.split(",") ?: emptyList()
            val buildingId = queryParameters["buildingId"]?.toIntOrNull()

            val rooms = roomService.getAllRoomsForAdmin(
                capacity = capacity,
                buildingId = buildingId,
                requiredEquipment = equipment
            )

            call.respond(rooms)
        }

        /**
         * Create a room. Only accessible by admins.
         *
         * @tag Admin
         * @body application/json [CreateRoomRequest] Room details.
         * @response 201 application/json [RoomResponse] Room created successfully.
         * @response 400 text/plain Invalid request data
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain Building not found
         * @response 500 text/plain Internal server error
         */
        post("/rooms") {
            runCatching {
                val request = call.receive<CreateRoomRequest>()
                roomService.createRoom(request)
            }.onSuccess { room ->
                call.respond(HttpStatusCode.Created, room)
            }.onFailure { e ->
                when (e) {
                    is RoomValidationException -> call.respondText(
                        e.message ?: "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )

                    is BuildingNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        /**
         * Update a room. Only accessible by admins.
         *
         * @tag Admin
         * @path roomId [Int] The ID of the room to update.
         * @body application/json [UpdateRoomRequest] Updated room details.
         * @response 202 application/json [RoomResponse] Room updated successfully.
         * @response 400 text/plain Invalid request data
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain Room or building not found
         * @response 500 text/plain Internal server error
         */
        put("/rooms/{roomId}") {
            val roomId = call.parameters["roomId"]?.toIntOrNull()
            if (roomId == null) {
                call.respondText(text = "Invalid Room ID", status = HttpStatusCode.BadRequest)
                return@put
            }

            runCatching {
                val request = call.receive<UpdateRoomRequest>()
                roomService.updateRoom(roomId, request)
            }.onSuccess { room ->
                call.respond(HttpStatusCode.Accepted, room)
            }.onFailure { e ->
                when (e) {
                    is RoomValidationException -> call.respondText(
                        e.message ?: "Bad Request",
                        status = HttpStatusCode.BadRequest
                    )

                    is RoomNotFoundException, is BuildingNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    else -> call.respondText(
                        e.message ?: "Internal Server Error",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }

        /**
         * Delete a room. Only accessible by admins.
         *
         * @tag Admin
         * @path roomId [Int] The ID of the room to delete.
         * @response 204 Room deleted successfully
         * @response 400 text/plain Invalid room ID
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 404 text/plain Room not found
         * @response 409 application/json [RoomDeletionConflictResponse] Room deletion blocked by dependencies
         * @response 500 text/plain Internal server error
         */
        delete("/rooms/{roomId}") {
            val roomId = call.parameters["roomId"]?.toIntOrNull()
            if (roomId == null) {
                call.respondText(text = "Invalid Room ID", status = HttpStatusCode.BadRequest)
                return@delete
            }

            runCatching {
                roomService.deleteRoom(roomId)
            }.onSuccess {
                call.respond(HttpStatusCode.NoContent)
            }.onFailure { e ->
                when (e) {
                    is RoomNotFoundException -> call.respondText(
                        e.message ?: "Not Found",
                        status = HttpStatusCode.NotFound
                    )

                    is RoomDeletionBlockedException -> call.respond(
                        HttpStatusCode.Conflict,
                        e.conflict
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
}
