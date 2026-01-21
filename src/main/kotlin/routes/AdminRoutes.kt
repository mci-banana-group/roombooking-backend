package edu.mci.routes

import edu.mci.model.api.response.AdminDashboardResponse
import edu.mci.service.AdminService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime

fun Route.adminRoutes(adminService: AdminService) {
    route("/admin") {
        /**
         * Get dashboard statistics for admins.
         *
         * @tag Admin
         * @query start [String] Start date-time (ISO 8601). Format: 2026-01-01T00:00:00
         * @query end [String] End date-time (ISO 8601). Format: 2026-01-31T23:59:59
         * @query limit [Int] Max number of most searched items to return (default 10, max 100).
         * @response 200 application/json [AdminDashboardResponse] Statistics for the dashboard.
         * @response 401 text/plain Unauthorized
         * @response 403 text/plain Forbidden (not an admin)
         * @response 400 text/plain Invalid date format or limit
         */
        get("/stats") {
            if (!call.isAdmin()) {
                call.respond(HttpStatusCode.Forbidden, "Only admins can access this resource")
                return@get
            }

            val queryParams = call.request.queryParameters
            val startStr = queryParams["start"]
            val endStr = queryParams["end"]
            val limitStr = queryParams["limit"]

            if (startStr == null || endStr == null) {
                call.respond(HttpStatusCode.BadRequest, "Start and end date-time are required")
                return@get
            }

            val start = runCatching { LocalDateTime.parse(startStr) }.getOrNull()
            val end = runCatching { LocalDateTime.parse(endStr) }.getOrNull()

            if (start == null || end == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid date-time format. Use ISO 8601 (e.g., 2026-01-01T00:00:00)")
                return@get
            }

            val limit = limitStr?.toIntOrNull() ?: 10
            if (limit <= 0) {
                call.respond(HttpStatusCode.BadRequest, "Limit must be a positive integer")
                return@get
            }
            val finalLimit = limit.coerceAtMost(100)

            val stats = adminService.getDashboardStats(start, end, finalLimit)
            call.respond(stats)
        }
    }
}
