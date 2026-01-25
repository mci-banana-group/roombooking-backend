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
        val timeZone = TimeZone.currentSystemDefault()
        val nowInstant = Clock.System.now()
        fun t(days: Int = 0, hours: Int = 0): LocalDateTime {
            val period = DateTimePeriod(days = days, hours = hours)
            return nowInstant.plus(period, timeZone).toLocalDateTime(timeZone)
        }

        // Users
        val admin = User.new {
            email = "admin@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Admin"
            lastName = "User"
            permissionLevel = PermissionLevel.ADMIN
            role = Role.STAFF
        }

        val staff1 = User.new {
            email = "sara.staff@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Sara"
            lastName = "Klein"
            permissionLevel = PermissionLevel.USER
            role = Role.STAFF
        }

        val staff2 = User.new {
            email = "milan.staff@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Milan"
            lastName = "Petrovic"
            permissionLevel = PermissionLevel.USER
            role = Role.STAFF
        }

        val lecturer1 = User.new {
            email = "lecturer@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "John"
            lastName = "Doe"
            permissionLevel = PermissionLevel.USER
            role = Role.LECTURER
        }

        val lecturer2 = User.new {
            email = "mia.lecturer@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Mia"
            lastName = "Huber"
            permissionLevel = PermissionLevel.USER
            role = Role.LECTURER
        }

        val lecturer3 = User.new {
            email = "david.lecturer@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "David"
            lastName = "Nguyen"
            permissionLevel = PermissionLevel.USER
            role = Role.LECTURER
        }

        val student1 = User.new {
            email = "lena.student@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Lena"
            lastName = "Gruber"
            permissionLevel = PermissionLevel.USER
            role = Role.STUDENT
        }

        val student2 = User.new {
            email = "tom.student@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Tom"
            lastName = "Mayer"
            permissionLevel = PermissionLevel.USER
            role = Role.STUDENT
        }

        val student3 = User.new {
            email = "nina.student@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Nina"
            lastName = "Schmid"
            permissionLevel = PermissionLevel.USER
            role = Role.STUDENT
        }

        val student4 = User.new {
            email = "armin.student@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Armin"
            lastName = "Balic"
            permissionLevel = PermissionLevel.USER
            role = Role.STUDENT
        }

        // Buildings
        val mainBuilding = Building.new {
            name = "Main Building"
            address = "Universitatsstrasse 15, 6020 Innsbruck"
        }

        val techCenter = Building.new {
            name = "Tech Center"
            address = "Technikerstrasse 12, 6020 Innsbruck"
        }

        val libraryAnnex = Building.new {
            name = "Library Annex"
            address = "Fritz-Prior-Strasse 1, 6020 Innsbruck"
        }

        val innovationHub = Building.new {
            name = "Innovation Hub"
            address = "Maria-Theresien-Strasse 25, 6020 Innsbruck"
        }

        val sportsHall = Building.new {
            name = "Sports Hall"
            address = "Innrain 52, 6020 Innsbruck"
        }

        // Rooms
        fun createRoom(
            number: Int,
            name: String,
            description: String,
            status: RoomStatus,
            code: String,
            capacity: Int,
            building: Building
        ): Room = Room.new {
            roomNumber = number
            this.name = name
            this.description = description
            this.status = status
            confirmationCode = code
            this.capacity = capacity
            this.building = building
        }

        fun addEquipment(room: Room, type: EquipmentType, quantity: Int) {
            RoomEquipmentItem.new {
                this.quantity = quantity
                this.type = type
                this.room = room
            }
        }

        val room101 = createRoom(
            number = 101,
            name = "Seminar Room A",
            description = "A small seminar room",
            status = RoomStatus.FREE,
            code = "MB-101",
            capacity = 20,
            building = mainBuilding
        )

        val room102 = createRoom(
            number = 102,
            name = "Seminar Room B",
            description = "Seminar room with flexible seating",
            status = RoomStatus.RESERVED,
            code = "MB-102",
            capacity = 24,
            building = mainBuilding
        )

        val room103 = createRoom(
            number = 103,
            name = "Lecture Hall 1",
            description = "Tiered seating with media control desk",
            status = RoomStatus.FREE,
            code = "MB-103",
            capacity = 80,
            building = mainBuilding
        )

        val room201 = createRoom(
            number = 201,
            name = "Project Studio",
            description = "Open room for group work",
            status = RoomStatus.OCCUPIED,
            code = "MB-201",
            capacity = 30,
            building = mainBuilding
        )

        val room202 = createRoom(
            number = 202,
            name = "Conference Room",
            description = "Large conference room with video wall",
            status = RoomStatus.FREE,
            code = "MB-202",
            capacity = 18,
            building = mainBuilding
        )

        val room203 = createRoom(
            number = 203,
            name = "Thesis Room",
            description = "Quiet room for thesis defenses",
            status = RoomStatus.RESERVED,
            code = "MB-203",
            capacity = 12,
            building = mainBuilding
        )

        val room204 = createRoom(
            number = 204,
            name = "Workshop Studio",
            description = "Hands-on studio with movable tables",
            status = RoomStatus.FREE,
            code = "MB-204",
            capacity = 26,
            building = mainBuilding
        )

        val tech110 = createRoom(
            number = 110,
            name = "Computer Lab 1",
            description = "PC lab with dual displays",
            status = RoomStatus.FREE,
            code = "TC-110",
            capacity = 28,
            building = techCenter
        )

        val tech120 = createRoom(
            number = 120,
            name = "Robotics Lab",
            description = "Lab with workbenches and sensors",
            status = RoomStatus.OCCUPIED,
            code = "TC-120",
            capacity = 16,
            building = techCenter
        )

        val tech210 = createRoom(
            number = 210,
            name = "Innovation Classroom",
            description = "Modular furniture and wall whiteboards",
            status = RoomStatus.FREE,
            code = "TC-210",
            capacity = 32,
            building = techCenter
        )

        val tech220 = createRoom(
            number = 220,
            name = "Makerspace",
            description = "3D printers and prototyping tools",
            status = RoomStatus.RESERVED,
            code = "TC-220",
            capacity = 14,
            building = techCenter
        )

        val tech230 = createRoom(
            number = 230,
            name = "Network Lab",
            description = "Lab with switches and routers",
            status = RoomStatus.FREE,
            code = "TC-230",
            capacity = 18,
            building = techCenter
        )

        val tech240 = createRoom(
            number = 240,
            name = "AR/VR Studio",
            description = "Immersive lab with VR headsets",
            status = RoomStatus.RESERVED,
            code = "TC-240",
            capacity = 12,
            building = techCenter
        )

        val lib10 = createRoom(
            number = 10,
            name = "Quiet Study 1",
            description = "Silent study room",
            status = RoomStatus.FREE,
            code = "LA-010",
            capacity = 12,
            building = libraryAnnex
        )

        val lib11 = createRoom(
            number = 11,
            name = "Quiet Study 2",
            description = "Silent study room with window wall",
            status = RoomStatus.FREE,
            code = "LA-011",
            capacity = 10,
            building = libraryAnnex
        )

        val lib20 = createRoom(
            number = 20,
            name = "Group Study 1",
            description = "Group study with whiteboards",
            status = RoomStatus.RESERVED,
            code = "LA-020",
            capacity = 8,
            building = libraryAnnex
        )

        val lib21 = createRoom(
            number = 21,
            name = "Group Study 2",
            description = "Small group room with display",
            status = RoomStatus.FREE,
            code = "LA-021",
            capacity = 8,
            building = libraryAnnex
        )

        val lib30 = createRoom(
            number = 30,
            name = "Media Study",
            description = "Room with media editing setup",
            status = RoomStatus.RESERVED,
            code = "LA-030",
            capacity = 6,
            building = libraryAnnex
        )

        val hub301 = createRoom(
            number = 301,
            name = "Startup Garage",
            description = "Pitch room with movable panels",
            status = RoomStatus.FREE,
            code = "IH-301",
            capacity = 26,
            building = innovationHub
        )

        val hub302 = createRoom(
            number = 302,
            name = "Design Lab",
            description = "Creative lab with sketch walls",
            status = RoomStatus.FREE,
            code = "IH-302",
            capacity = 20,
            building = innovationHub
        )

        val hub401 = createRoom(
            number = 401,
            name = "Board Room",
            description = "Executive board room",
            status = RoomStatus.RESERVED,
            code = "IH-401",
            capacity = 14,
            building = innovationHub
        )

        val hub402 = createRoom(
            number = 402,
            name = "Strategy Room",
            description = "Planning room with wall displays",
            status = RoomStatus.FREE,
            code = "IH-402",
            capacity = 16,
            building = innovationHub
        )

        val hub501 = createRoom(
            number = 501,
            name = "Investor Lounge",
            description = "Private meeting room for investors",
            status = RoomStatus.RESERVED,
            code = "IH-501",
            capacity = 10,
            building = innovationHub
        )

        val sports1 = createRoom(
            number = 1,
            name = "Gym Hall",
            description = "Multi-purpose sports hall",
            status = RoomStatus.OCCUPIED,
            code = "SH-001",
            capacity = 60,
            building = sportsHall
        )

        val sports2 = createRoom(
            number = 2,
            name = "Dance Studio",
            description = "Studio with mirrors and wooden floor",
            status = RoomStatus.FREE,
            code = "SH-002",
            capacity = 25,
            building = sportsHall
        )

        val sports3 = createRoom(
            number = 3,
            name = "Yoga Room",
            description = "Quiet space for yoga and stretching",
            status = RoomStatus.FREE,
            code = "SH-003",
            capacity = 18,
            building = sportsHall
        )

        addEquipment(room101, EquipmentType.BEAMER, 1)
        addEquipment(room101, EquipmentType.WHITEBOARD, 2)
        addEquipment(room102, EquipmentType.BEAMER, 1)
        addEquipment(room102, EquipmentType.HDMI_CABLE, 2)
        addEquipment(room103, EquipmentType.BEAMER, 2)
        addEquipment(room103, EquipmentType.DISPLAY, 1)
        addEquipment(room201, EquipmentType.WHITEBOARD, 3)
        addEquipment(room201, EquipmentType.HDMI_CABLE, 2)
        addEquipment(room202, EquipmentType.DISPLAY, 2)
        addEquipment(room202, EquipmentType.BEAMER, 1)
        addEquipment(room203, EquipmentType.WHITEBOARD, 1)
        addEquipment(room204, EquipmentType.BEAMER, 1)
        addEquipment(room204, EquipmentType.WHITEBOARD, 2)
        addEquipment(tech110, EquipmentType.DISPLAY, 28)
        addEquipment(tech110, EquipmentType.HDMI_CABLE, 20)
        addEquipment(tech120, EquipmentType.WHITEBOARD, 2)
        addEquipment(tech120, EquipmentType.DISPLAY, 4)
        addEquipment(tech210, EquipmentType.BEAMER, 1)
        addEquipment(tech210, EquipmentType.WHITEBOARD, 2)
        addEquipment(tech220, EquipmentType.BEAMER, 1)
        addEquipment(tech220, EquipmentType.HDMI_CABLE, 4)
        addEquipment(tech230, EquipmentType.DISPLAY, 6)
        addEquipment(tech230, EquipmentType.HDMI_CABLE, 6)
        addEquipment(tech240, EquipmentType.DISPLAY, 4)
        addEquipment(lib10, EquipmentType.WHITEBOARD, 1)
        addEquipment(lib11, EquipmentType.WHITEBOARD, 1)
        addEquipment(lib20, EquipmentType.DISPLAY, 1)
        addEquipment(lib21, EquipmentType.DISPLAY, 1)
        addEquipment(lib30, EquipmentType.DISPLAY, 1)
        addEquipment(hub301, EquipmentType.BEAMER, 1)
        addEquipment(hub301, EquipmentType.WHITEBOARD, 2)
        addEquipment(hub302, EquipmentType.BEAMER, 1)
        addEquipment(hub302, EquipmentType.DISPLAY, 2)
        addEquipment(hub401, EquipmentType.DISPLAY, 1)
        addEquipment(hub401, EquipmentType.HDMI_CABLE, 2)
        addEquipment(hub402, EquipmentType.DISPLAY, 2)
        addEquipment(hub501, EquipmentType.DISPLAY, 1)
        addEquipment(sports1, EquipmentType.DISPLAY, 1)
        addEquipment(sports2, EquipmentType.DISPLAY, 1)
        addEquipment(sports3, EquipmentType.DISPLAY, 1)

        // Bookings
        val booking1 = Booking.new {
            start = t(hours = 2)
            end = t(hours = 4)
            createdAt = t(hours = -1)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Lecturer Meeting"
            user = lecturer1
            room = room101
        }

        val booking2 = Booking.new {
            start = t(hours = -4)
            end = t(hours = -2)
            createdAt = t(hours = -6)
            gracePeriodMin = 10
            status = BookingStatus.CHECKED_IN
            description = "Project Sync"
            user = lecturer2
            room = room201
        }

        val booking3 = Booking.new {
            start = t(days = 1, hours = 1)
            end = t(days = 1, hours = 3)
            createdAt = t(hours = -2)
            gracePeriodMin = 20
            status = BookingStatus.RESERVED
            description = "Data Science Lab"
            user = lecturer3
            room = tech110
        }

        val booking4 = Booking.new {
            start = t(days = 2, hours = 4)
            end = t(days = 2, hours = 6)
            createdAt = t(days = -1)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Robotics Demo"
            user = staff1
            room = tech120
        }

        val booking5 = Booking.new {
            start = t(days = -1, hours = -2)
            end = t(days = -1)
            createdAt = t(days = -2)
            gracePeriodMin = 10
            status = BookingStatus.NO_SHOW
            description = "Study Group"
            user = student1
            room = lib20
        }

        val booking6 = Booking.new {
            start = t(days = 3, hours = 2)
            end = t(days = 3, hours = 5)
            createdAt = t(days = -1)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Startup Pitch Prep"
            user = student2
            room = hub301
        }

        val booking7 = Booking.new {
            start = t(hours = 6)
            end = t(hours = 8)
            createdAt = t(hours = -3)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Board Review"
            user = staff2
            room = hub401
        }

        val booking8 = Booking.new {
            start = t(hours = -10)
            end = t(hours = -8)
            createdAt = t(hours = -12)
            gracePeriodMin = 10
            status = BookingStatus.CANCELLED
            description = "Dance Workshop"
            user = student3
            room = sports2
        }

        val booking9 = Booking.new {
            start = t(days = 4, hours = 1)
            end = t(days = 4, hours = 3)
            createdAt = t(days = 1)
            gracePeriodMin = 20
            status = BookingStatus.RESERVED
            description = "Guest Lecture"
            user = lecturer1
            room = room103
        }

        val booking10 = Booking.new {
            start = t(hours = -3)
            end = t(hours = -1)
            createdAt = t(hours = -5)
            gracePeriodMin = 15
            status = BookingStatus.CHECKED_IN
            description = "Marketing Workshop"
            user = student4
            room = room202
        }

        PresenceConfirmation.new {
            timestamp = t(hours = -3)
            method = ConfirmationMethod.QR_CODE
            booking = booking2
        }

        PresenceConfirmation.new {
            timestamp = t(hours = -2)
            method = ConfirmationMethod.NFC
            booking = booking10
        }

        Notification.new {
            sentAt = t(hours = -1)
            message = "Reminder: Lecturer Meeting in Seminar Room A"
            channel = NotificationChannel.EMAIL
            user = lecturer1
            booking = booking1
        }

        Notification.new {
            sentAt = t(hours = -5)
            message = "Check-in confirmed for Project Sync"
            channel = NotificationChannel.PUSH
            user = lecturer2
            booking = booking2
        }

        Notification.new {
            sentAt = t(days = -1, hours = -1)
            message = "Booking marked as no-show for Study Group"
            channel = NotificationChannel.EMAIL
            user = student1
            booking = booking5
        }
    }

    environment.log.info("Database seeded with dummy data.")
}
