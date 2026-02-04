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
        // Fixed reference date: 05.02.2026 10:00
        val fixedNow = LocalDateTime(2026, 2, 5, 10, 0)
        val nowInstant = fixedNow.toInstant(timeZone)

        // Global counter for room numbers
        var nextRoomNumber = 1

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
            email = "baran.sagis@mci.edu"
            password = passwordService.hashPassword("password")
            firstName = "Baran"
            lastName = "Sagis"
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

        // --- Realistic Buildings (MCI I - V) ---
        val mci1 = Building.new {
            name = "MCI I"
            address = "Universitätsstraße 15, 6020 Innsbruck"
        }

        val mci2 = Building.new {
            name = "MCI II"
            address = "Universitätsstraße 7, 6020 Innsbruck"
        }

        val mci3 = Building.new {
            name = "MCI III"
            address = "Weiherburggasse 8, 6020 Innsbruck"
        }

        val mci4 = Building.new {
            name = "MCI IV"
            address = "Maximilianstraße 2, 6020 Innsbruck"
        }

        val mci5 = Building.new {
            name = "MCI V"
            address = "Kapuzinergasse 9, 6020 Innsbruck"
        }

        // Room Helper
        fun createRoom(
            name: String, // This is the short code from the colum RAUM (e.g. "4B-001")
            capacity: Int,
            building: Building,
            seating: String = "Schule mit Mittelgang", // in the xlsx defined as the Standard.
            status: RoomStatus = RoomStatus.FREE
        ): Room {
            return Room.new {
                roomNumber = nextRoomNumber++
                this.name = name
                this.description = seating
                this.status = status
                confirmationCode = "1234"
                this.capacity = capacity
                this.building = building
            }
        }


        fun addEquipment(room: Room, type: EquipmentType, quantity: Int) {
            RoomEquipmentItem.new {
                this.quantity = quantity
                this.type = type
                this.room = room
            }
        }

        // --- Realistic Rooms (Based on Standardbestuhlung 2025.xlsx) ---

        // MCI I
        val mci1_234 = createRoom("234", 26, mci1)
        val mci1_301 = createRoom("301", 66, mci1)
        val mci1_302 = createRoom("302", 66, mci1)
        val mci1_303 = createRoom("303", 54, mci1)
        val mci1_304 = createRoom("304", 54, mci1)
        val mci1_305 = createRoom("305", 60, mci1)
        val mci1_306 = createRoom("306", 56, mci1)
        val mci1_307 = createRoom("307", 32, mci1)
        val mci1_308 = createRoom("308", 18, mci1, "Konferenzraum")
        val mci1_309 = createRoom("309", 45, mci1)
        val mci1_310 = createRoom("310", 32, mci1)
        val mci1_401_402 = createRoom("401/402", 20, mci1, "U-Bestuhlung")
        val mci1_403 = createRoom("403", 28, mci1, "Parlament")
        val mci1_404 = createRoom("404", 28, mci1, "Parlament")
        val mci1_405 = createRoom("405", 28, mci1, "Parlament")
        val mci1_406 = createRoom("406", 32, mci1, "Parlament")

        // MCI II
        val mci2_051 = createRoom("051", 61, mci2)
        val mci2_052 = createRoom("052", 69, mci2)
        val mci2_053 = createRoom("053", 78, mci2)
        val mci2_162 = createRoom("162", 8, mci2, "WOW-Raum")
        val mci2_163 = createRoom("163", 8, mci2, "WOW-Raum")
        val mci2_164 = createRoom("164", 12, mci2, "Besprechungsraum")
        val mci2_551 = createRoom("551", 40, mci2)
        val mci2_552 = createRoom("552", 50, mci2)

        // MCI III
        val mci3_011 = createRoom("011", 54, mci3)
        val mci3_012 = createRoom("012", 54, mci3)
        val mci3_013 = createRoom("013", 54, mci3)
        val mci3_014 = createRoom("014", 30, mci3)
        val mci3_111 = createRoom("111", 54, mci3)
        val mci3_112 = createRoom("112", 60, mci3)
        val mci3_113 = createRoom("113", 30, mci3)

        // MCI IV
        val mci4_4B001 = createRoom("4B-001", 29, mci4)
        val mci4_4B003 = createRoom("4B-003", 30, mci4)
        val mci4_4B005 = createRoom("4B-005", 23, mci4)
        val mci4_4B006 = createRoom("4B-006", 33, mci4)
        val mci4_4B007 = createRoom("4B-007", 30, mci4)
        val mci4_4B008 = createRoom("4B-008", 16, mci4, "Besprechungsraum")
        val mci4_4A020 = createRoom("4A-020", 58, mci4)
        val mci4_4A024 = createRoom("4A-024", 68, mci4)
        val mci4_4A027 = createRoom("4A-027", 60, mci4)
        val mci4_1A135 = createRoom("1A-135", 45, mci4, "e-exam-Raum/SR")
        val mci4_4B115 = createRoom("4B-115", 22, mci4, "EDV-Raum")
        val mci4_4A393 = createRoom("4A-393", 48, mci4)
        val mci4_4A438 = createRoom("4A-438", 36, mci4)
        val mci4_4A439 = createRoom("4A-439", 68, mci4)
        val mci4_4C501 = createRoom("4C-501", 12, mci4)
        val mci4_4C502 = createRoom("4C-502", 25, mci4, "EDV-Raum")
        val mci4_4C503 = createRoom("4C-503", 25, mci4, "EDV-Raum")
        val mci4_4C504 = createRoom("4C-504", 27, mci4, "EDV-Raum")
        val mci4_4C505 = createRoom("4C-505", 45, mci4, "EDV-Raum")

        // MCI V
        val mci5_181 = createRoom("181", 40, mci5)
        val mci5_182 = createRoom("182", 40, mci5)
        val mci5_183 = createRoom("183", 40, mci5)
        val mci5_184 = createRoom("184", 44, mci5)
        val mci5_185 = createRoom("185", 52, mci5)
        val mci5_283 = createRoom("283", 40, mci5)


        // Equipment Seeding (Assigned to realistic rooms)
        // Standard Setup for Seminar Rooms: Beamer + Whiteboards
        listOf(mci1_301, mci1_302, mci4_4A020, mci4_4A024).forEach {
            addEquipment(it, EquipmentType.BEAMER, 1)
            addEquipment(it, EquipmentType.WHITEBOARD, 2)
            addEquipment(it, EquipmentType.HDMI_CABLE, 1)
        }

        // Setup for EDV/Computer Rooms: Displays
        listOf(mci4_4B115, mci4_4C502, mci4_4C503).forEach {
            addEquipment(it, EquipmentType.DISPLAY, it.capacity) // One per student-seat approx
            addEquipment(it, EquipmentType.WHITEBOARD, 1)
        }

        // Setup for Meeting/Small Rooms
        listOf(mci1_308, mci2_164, mci4_4B008).forEach {
            addEquipment(it, EquipmentType.DISPLAY, 1)
            addEquipment(it, EquipmentType.HDMI_CABLE, 1)
        }

        val allRooms = listOf(
            mci1_234, mci1_301, mci1_302, mci1_303, mci1_304, mci1_305, mci1_306, mci1_307, mci1_308,
            mci1_309, mci1_310, mci1_401_402, mci1_403, mci1_404, mci1_405, mci1_406,
            mci2_051, mci2_052, mci2_053, mci2_162, mci2_163, mci2_164, mci2_551, mci2_552,
            mci3_011, mci3_012, mci3_013, mci3_014, mci3_111, mci3_112, mci3_113,
            mci4_4B001, mci4_4B003, mci4_4B005, mci4_4B006, mci4_4B007, mci4_4B008,
            mci4_4A020, mci4_4A024, mci4_4A027, mci4_1A135, mci4_4B115, mci4_4A393, mci4_4A438,
            mci4_4A439, mci4_4C501, mci4_4C502, mci4_4C503, mci4_4C504, mci4_4C505,
            mci5_181, mci5_182, mci5_183, mci5_184, mci5_185, mci5_283
        )

        val allUsers = listOf(admin, staff1, staff2, lecturer1, lecturer2, lecturer3, student1, student2, student3, student4)
        // Exclude Baran (student1) and Admin from general noise bookings to keep their counts controlled
        val cyclicalUsers = allUsers.filter { it != student1 && it != admin }

        val today = nowInstant.toLocalDateTime(timeZone).date
        val pastDates = (3..90 step 6).map { offset -> today.minus(DatePeriod(days = offset)) }
        val futureDates = (1..30 step 5).map { offset -> today.plus(DatePeriod(days = offset)) }
        val demoDate = LocalDate(2026, 2, 5)
        val demoRooms = setOf(mci1_308, mci2_164, mci4_4B008, mci4_4C501)
        val futureDatesBase = futureDates.filter { it != demoDate }.ifEmpty { futureDates }
        val spreadDates = (-14..14).map { offset -> fixedNow.date.plus(DatePeriod(days = offset)) }.filter { it != demoDate }
        val nearPastDates = (1..7).map { offset -> today.minus(DatePeriod(days = offset)) }
        val nearFutureDates = (0..14).map { offset -> today.plus(DatePeriod(days = offset)) }.filter { it != demoDate }

        val pastStatuses = listOf(
            BookingStatus.COMPLETED,
            BookingStatus.COMPLETED,
            BookingStatus.NO_SHOW,
            BookingStatus.CANCELLED,
            BookingStatus.ADMIN_CANCELLED
        )
        val currentStatuses = listOf(
            BookingStatus.RESERVED,
            BookingStatus.CHECKED_IN,
            BookingStatus.RESERVED
        )
        val futureStatuses = listOf(
            BookingStatus.RESERVED,
            BookingStatus.RESERVED,
            BookingStatus.RESERVED,
            BookingStatus.CANCELLED,
            BookingStatus.ADMIN_CANCELLED
        )

        fun slotStart(date: LocalDate, slot: Int): LocalDateTime {
            val hour = 8 + ((slot % 5) * 2)
            return LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, 0)
        }

        fun addSeedBooking(
            date: LocalDate,
            slot: Int,
            durationHours: Int,
            status: BookingStatus,
            user: User,
            room: Room,
            description: String
        ): Booking {
            val start = slotStart(date, slot)
            val end = start.toInstant(timeZone).plus(durationHours, DateTimeUnit.HOUR).toLocalDateTime(timeZone)
            val createdAt = start.toInstant(timeZone).minus(6, DateTimeUnit.HOUR).toLocalDateTime(timeZone)
            return Booking.new {
                this.start = start
                this.end = end
                this.createdAt = createdAt
                gracePeriodMin = 15
                this.status = status
                this.description = description
                this.user = user
                this.room = room
            }
        }

        val descriptions = listOf(
            "Seminar",
            "Team Meeting",
            "Lab Session",
            "Exam Prep",
            "Workshop",
            "Study Group",
            "Project Review",
            "Guest Lecture",
            "Thesis Defense",
            "Career Session"
        )
        var bookingCounter = 0
        fun nextDescription(): String {
            val description = descriptions[bookingCounter % descriptions.size]
            bookingCounter += 1
            return description
        }

        // Bookings
        val booking1 = Booking.new {
            start = t(hours = 2)
            end = t(hours = 4)
            createdAt = t(hours = -1)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Lecturer Meeting"
            user = lecturer1
            room = mci1_301
        }

        val booking2 = Booking.new {
            start = t(hours = -4)
            end = t(hours = -2)
            createdAt = t(hours = -6)
            gracePeriodMin = 10
            status = BookingStatus.CHECKED_IN
            description = "Project Sync"
            user = lecturer2
            room = mci2_164
        }

        val booking3 = Booking.new {
            start = t(days = 1, hours = 1)
            end = t(days = 1, hours = 3)
            createdAt = t(hours = -2)
            gracePeriodMin = 20
            status = BookingStatus.RESERVED
            description = "Data Science Lab"
            user = lecturer3
            room = mci4_4B115
        }

        val booking4 = Booking.new {
            start = t(days = 2, hours = 4)
            end = t(days = 2, hours = 6)
            createdAt = t(days = -1)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Robotics Demo"
            user = staff1
            room = mci4_4C502
        }

        val booking5 = Booking.new {
            start = t(days = -1, hours = -2)
            end = t(days = -1)
            createdAt = t(days = -2)
            gracePeriodMin = 10
            status = BookingStatus.NO_SHOW
            description = "Study Group"
            user = student1
            room = mci3_014
        }

        val booking6 = Booking.new {
            start = t(days = 3, hours = 2)
            end = t(days = 3, hours = 5)
            createdAt = t(days = -1)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Startup Pitch Prep"
            user = student2
            room = mci5_181
        }

        val booking7 = Booking.new {
            start = t(hours = 6)
            end = t(hours = 8)
            createdAt = t(hours = -3)
            gracePeriodMin = 15
            status = BookingStatus.RESERVED
            description = "Board Review"
            user = staff2
            room = mci1_308
        }

        val booking8 = Booking.new {
            start = t(hours = -10)
            end = t(hours = -8)
            createdAt = t(hours = -12)
            gracePeriodMin = 10
            status = BookingStatus.CANCELLED
            description = "Student Union Workshop"
            user = student3
            room = mci1_401_402
        }

        val booking9 = Booking.new {
            start = t(days = 4, hours = 1)
            end = t(days = 4, hours = 3)
            createdAt = t(days = 1)
            gracePeriodMin = 20
            status = BookingStatus.RESERVED
            description = "Guest Lecture"
            user = lecturer1
            room = mci4_4A020
        }

        val booking10 = Booking.new {
            start = t(hours = -3)
            end = t(hours = -1)
            createdAt = t(hours = -5)
            gracePeriodMin = 15
            status = BookingStatus.CHECKED_IN
            description = "Marketing Workshop"
            user = student4
            room = mci5_185
        }

        allRooms.forEachIndexed { index, room ->
            spreadDates.forEachIndexed { dayIndex, date ->
                addSeedBooking(
                    date = date,
                    slot = index + dayIndex,
                    durationHours = 2,
                    status = pastStatuses[(index + dayIndex) % pastStatuses.size],
                    user = cyclicalUsers[(index + dayIndex) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
                addSeedBooking(
                    date = date,
                    slot = index + dayIndex + 1,
                    durationHours = 2,
                    status = pastStatuses[(index + dayIndex + 2) % pastStatuses.size],
                    user = cyclicalUsers[(index + dayIndex + 3) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
            }

            nearPastDates.forEachIndexed { dayIndex, date ->
                addSeedBooking(
                    date = date,
                    slot = index + dayIndex,
                    durationHours = 2,
                    status = pastStatuses[(index + dayIndex) % pastStatuses.size],
                    user = cyclicalUsers[(index + dayIndex) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
                addSeedBooking(
                    date = date,
                    slot = index + dayIndex + 2,
                    durationHours = 2,
                    status = pastStatuses[(index + dayIndex + 2) % pastStatuses.size],
                    user = cyclicalUsers[(index + dayIndex + 3) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
            }

            nearFutureDates.forEachIndexed { dayIndex, date ->
                addSeedBooking(
                    date = date,
                    slot = index + dayIndex + 1,
                    durationHours = 2,
                    status = futureStatuses[(index + dayIndex) % futureStatuses.size],
                    user = cyclicalUsers[(index + dayIndex + 1) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
                addSeedBooking(
                    date = date,
                    slot = index + dayIndex + 3,
                    durationHours = 2,
                    status = futureStatuses[(index + dayIndex + 2) % futureStatuses.size],
                    user = cyclicalUsers[(index + dayIndex + 3) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
            }

            val pastDate1 = pastDates[(index * 3) % pastDates.size]
            val pastDate2 = pastDates[(index * 3 + 5) % pastDates.size]
            val pastDate3 = pastDates[(index * 3 + 11) % pastDates.size]

            listOf(pastDate1, pastDate2, pastDate3).distinct().forEachIndexed { i, date ->
                addSeedBooking(
                    date = date,
                    slot = index + i,
                    durationHours = 2,
                    status = pastStatuses[(index + i) % pastStatuses.size],
                    user = cyclicalUsers[(index + i) % cyclicalUsers.size],
                    room = room,
                    description = nextDescription()
                )
            }

            val currentDate = if (index % 2 == 0) today else today.minus(DatePeriod(days = 1))
            addSeedBooking(
                date = currentDate,
                slot = index + 2,
                durationHours = 2,
                status = currentStatuses[index % currentStatuses.size],
                user = cyclicalUsers[(index + 2) % cyclicalUsers.size],
                room = room,
                description = nextDescription()
            )

            val futureDate1 = futureDatesBase[(index * 2) % futureDatesBase.size]
            val futureDate2 = futureDatesBase[(index * 2 + 3) % futureDatesBase.size]
            addSeedBooking(
                date = futureDate1,
                slot = index + 1,
                durationHours = 2,
                status = futureStatuses[index % futureStatuses.size],
                user = cyclicalUsers[(index + 1) % cyclicalUsers.size],
                room = room,
                description = nextDescription()
            )
            addSeedBooking(
                date = futureDate2,
                slot = index + 3,
                durationHours = 2,
                status = futureStatuses[(index + 2) % futureStatuses.size],
                user = cyclicalUsers[(index + 3) % cyclicalUsers.size],
                room = room,
                description = nextDescription()
            )
        }

        demoRooms.forEachIndexed { index, room ->
            addSeedBooking(
                date = demoDate,
                slot = index,
                durationHours = 2,
                status = BookingStatus.RESERVED,
                user = cyclicalUsers[(index + 1) % cyclicalUsers.size],
                room = room,
                description = "Live Demo Hold"
            )
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
            message = "Reminder: Lecturer Meeting in Seminarraum 301"
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

        // --- Special Bookings for Baran (student1) ---
        val baranOffsets = listOf(-14, -8, -3, -1, 2, 4, 9, 15, 22, 28)
        val smallRooms = listOf(mci1_308, mci2_164, mci4_4B008, mci4_4C501)

        baranOffsets.forEachIndexed { index, offset ->
            val date = fixedNow.date.plus(DatePeriod(days = offset))
            val isPast = offset < 0
            val status = if (isPast) BookingStatus.COMPLETED else BookingStatus.RESERVED
            // Rotate rooms
            val room = smallRooms[index % smallRooms.size]

            addSeedBooking(
                date = date,
                slot = 1 + (index % 5), // Vary bookings between slots 1-5 (approx 10:00 - 18:00 start)
                durationHours = if (index % 3 == 0) 4 else 2,
                status = status,
                user = student1,
                room = room,
                description = "Self Study Session ${index + 1}"
            )

        }

        // --- Special Bookings for Admin ---
        val adminOffsets = listOf(-2, 3, 7, 12, 18)
        adminOffsets.forEachIndexed { index, offset ->
             addSeedBooking(
                date = fixedNow.date.plus(DatePeriod(days = offset)),
                slot = 2,
                durationHours = 1,
                status = BookingStatus.RESERVED,
                user = admin,
                room = mci1_308, // Admin likes the conference room
                description = "Admin Review ${index + 1}"
            )
        }

        // --- Working Hours Bookings for Feb 5 & 6 ---
        // Ensure some intensity on these specific days
        val workingDays = listOf(
             LocalDate(2026, 2, 5),
             LocalDate(2026, 2, 6)
        )
        val workingHourUsers = listOf(staff1, staff2, lecturer2, lecturer3) // Exclude limited users
        
        workingDays.forEach { date ->
             // Morning block
             addSeedBooking(
                 date = date,
                 slot = 0, // 08:00
                 durationHours = 3,
                 status = BookingStatus.RESERVED,
                 user = workingHourUsers[0],
                 room = mci4_4A027,
                 description = "Morning Workshop"
             )
             addSeedBooking(
                 date = date,
                 slot = 1, // 10:00 start (overlap slightly or adjacent)
                 durationHours = 2,
                 status = BookingStatus.RESERVED,
                 user = workingHourUsers[1],
                 room = mci3_111,
                 description = "Team Sync"
             )
             
             // Afternoon block
             addSeedBooking(
                 date = date,
                 slot = 3, // 14:00
                 durationHours = 2,
                 status = BookingStatus.RESERVED,
                 user = workingHourUsers[2],
                 room = mci1_301,
                 description = "Client Presentation"
             )
             addSeedBooking(
                 date = date,
                 slot = 4, // 16:00
                 durationHours = 1,
                 status = BookingStatus.RESERVED,
                 user = workingHourUsers[3],
                 room = mci5_185,
                 description = "Wrap-up"
             )
        }

        // --- Searched Items Seeding ---
        val searchTerms = listOf("Meeting", "Lab", "Seminar", "Project", "Workshop", "Exam", "Room 301", "MCI V", "Display", "Quiet", "Computer")
        repeat(50) {
            val randomDaysAgo = (0..30).random()
            val randomHoursAgo = (0..23).random()
            SearchedItem.new {
                searchTerm = searchTerms.random()
                searchedAt = t(days = -randomDaysAgo, hours = -randomHoursAgo)
            }
        }
    }

    environment.log.info("Database seeded with realistic MCI data.")
}
