package edu.mci.plugins

import edu.mci.model.db.*
import io.ktor.server.application.*
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.seedData() {
    transaction {
        if (User.count() > 0) return@transaction // Already seeded

        // Users
        val admin = User.new {
            email = "admin@mci.edu"
            password = "password"
            firstName = "Admin"
            lastName = "User"
            permissionLevel = PermissionLevel.ADMIN
            role = Role.STAFF
        }

        val lecturer = User.new {
            email = "lecturer@mci.edu"
            password = "password"
            firstName = "John"
            lastName = "Doe"
            permissionLevel = PermissionLevel.USER
            role = Role.LECTURER
        }

        // Buildings
        val mainBuilding = Building.new {
            name = "Main Building"
            address = "Universitätsstraße 15, 6020 Innsbruck"
        }

        // Rooms
        val room101 = Room.new {
            roomNumber = 101
            name = "Seminar Room A"
            description = "A small seminar room"
            status = RoomStatus.FREE
            confirmationCode = "CONF123"
            capacity = 20
            building = mainBuilding
        }

        RoomEquipmentItem.new {
            quantity = 1
            type = EquipmentType.BEAMER
            room = room101
        }

        // Bookings
        Booking.new {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            start = now
            end = Clock.System.now().plus(2, DateTimeUnit.HOUR).toLocalDateTime(TimeZone.currentSystemDefault())
            createdAt = now
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Lecturer Meeting"
            user = lecturer
            this.room = room101
        }
    }

    environment.log.info("Database seeded with dummy data.")
}
