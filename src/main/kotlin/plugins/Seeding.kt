package edu.mci.plugins

import edu.mci.model.db.*
import edu.mci.service.BCryptPasswordService
import io.ktor.server.application.*
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.seedData() {
    val passwordService = BCryptPasswordService()
    transaction {
        if (User.count() > 0) return@transaction // Already seeded

        // Users
        val admin = User.new {
            email = "admin@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Admin"
            lastName = "User"
            permissionLevel = PermissionLevel.ADMIN
            role = Role.STAFF
        }

        val lecturer = User.new {
            email = "lecturer@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "John"
            lastName = "Doe"
            permissionLevel = PermissionLevel.USER
            role = Role.LECTURER
        }

        val student = User.new {
            email = "student@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Jane"
            lastName = "Smith"
            permissionLevel = PermissionLevel.USER
            role = Role.STUDENT
        }

        // Buildings
        val mainBuilding = Building.new {
            name = "Main Building"
            address = "Universitätsstraße 15, 6020 Innsbruck"
        }

        val sideBuilding = Building.new {
            name = "Side Building"
            address = "Eggenhoferstraße 3, 6020 Innsbruck"
        }

        // Rooms
        val rooms = mutableListOf<Room>()

        rooms.add(Room.new {
            roomNumber = 101
            name = "Seminar Room A"
            description = "A small seminar room"
            status = RoomStatus.FREE
            confirmationCode = "CONF101"
            capacity = 20
            building = mainBuilding
        })

        rooms.add(Room.new {
            roomNumber = 102
            name = "Lecture Hall 1"
            description = "Large lecture hall"
            status = RoomStatus.FREE
            confirmationCode = "CONF102"
            capacity = 100
            building = mainBuilding
        })

        rooms.add(Room.new {
            roomNumber = 201
            name = "Project Room B"
            description = "Room for group work"
            status = RoomStatus.FREE
            confirmationCode = "CONF201"
            capacity = 10
            building = sideBuilding
        })

        // Equipment
        RoomEquipmentItem.new {
            quantity = 1
            type = EquipmentType.BEAMER
            room = rooms[0]
        }
        RoomEquipmentItem.new {
            quantity = 2
            type = EquipmentType.BEAMER
            room = rooms[1]
        }
        RoomEquipmentItem.new {
            quantity = 1
            type = EquipmentType.WHITEBOARD
            room = rooms[2]
        }
        RoomEquipmentItem.new {
            quantity = 1
            type = EquipmentType.DISPLAY
            room = rooms[2]
        }

        // Bookings
        val now = Clock.System.now()
        val users = listOf(lecturer, student)

        // Generate some historical bookings for stats
        for (i in 1..20) {
            val bookingStart = now.minus(i, DateTimeUnit.DAY, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
            val bookingEnd = now.minus(i, DateTimeUnit.DAY, TimeZone.UTC).plus(2, DateTimeUnit.HOUR, TimeZone.UTC)
                .toLocalDateTime(TimeZone.UTC)

            Booking.new {
                start = bookingStart
                end = bookingEnd
                createdAt = bookingStart.toInstant(TimeZone.UTC).minus(1, DateTimeUnit.DAY, TimeZone.UTC)
                    .toLocalDateTime(TimeZone.UTC)
                gracePeriodMin = 15
                status = if (i % 3 == 0) BookingStatus.CANCELLED else BookingStatus.CHECKED_IN
                description = "Past Meeting $i"
                user = users.random()
                room = rooms.random()
            }
        }

        // Generate some NO_SHOW bookings
        for (i in 1..5) {
            val bookingStart = now.minus(i + 20, DateTimeUnit.DAY, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
            val bookingEnd = now.minus(i + 20, DateTimeUnit.DAY, TimeZone.UTC).plus(1, DateTimeUnit.HOUR, TimeZone.UTC)
                .toLocalDateTime(TimeZone.UTC)

            Booking.new {
                start = bookingStart
                end = bookingEnd
                createdAt = bookingStart.toInstant(TimeZone.UTC).minus(2, DateTimeUnit.DAY, TimeZone.UTC)
                    .toLocalDateTime(TimeZone.UTC)
                gracePeriodMin = 15
                status = BookingStatus.NO_SHOW
                description = "Missed Meeting $i"
                user = users.random()
                room = rooms.random()
            }
        }

        // Future bookings
        for (i in 1..5) {
            val bookingStart = now.plus(i, DateTimeUnit.DAY, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
            val bookingEnd = now.plus(i, DateTimeUnit.DAY, TimeZone.UTC).plus(1, DateTimeUnit.HOUR, TimeZone.UTC)
                .toLocalDateTime(TimeZone.UTC)

            Booking.new {
                start = bookingStart
                end = bookingEnd
                createdAt = now.toLocalDateTime(TimeZone.UTC)
                gracePeriodMin = 15
                status = BookingStatus.RESERVED
                description = "Future Meeting $i"
                user = users.random()
                room = rooms.random()
            }
        }

        // Searched Items
        val searchTerms = listOf("BEAMER", "WHITEBOARD", "DISPLAY", "HDMI_CABLE")
        for (i in 1..50) {
            SearchedItem.new {
                searchTerm = searchTerms.random()
                searchedAt = now.minus(i % 10, DateTimeUnit.DAY, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
            }
            // Add some bias to BEAMER
            if (i % 2 == 0) {
                SearchedItem.new {
                    searchTerm = "BEAMER"
                    searchedAt = now.minus(i % 5, DateTimeUnit.DAY, TimeZone.UTC).toLocalDateTime(TimeZone.UTC)
                }
            }
        }
    }

    environment.log.info("Database seeded with extensive dummy data.")
}
