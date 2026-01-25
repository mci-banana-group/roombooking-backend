package edu.mci.repository

import edu.mci.model.db.*
import kotlinx.datetime.*
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

interface BookingRepository {
    fun findById(id: Int): Booking?
    fun findByUserId(userId: Int): List<Booking>
    fun findByRoomIdsAndDate(roomIds: List<Int>, date: LocalDate): List<Booking>
    fun findOverlappingBookings(roomId: Int, start: Instant, end: Instant): List<Booking>
    fun findExpiredReservations(dateTime: LocalDateTime): List<Booking>
    fun findBookingsCheckInWindow(dateTime: LocalDateTime): List<Booking>
    fun create(
        user: User,
        room: Room,
        start: Instant,
        end: Instant,
        description: String,
        gracePeriodMin: Int,
        confirmationCode: String,
    ): Booking

    fun update(
        booking: Booking,
        room: Room,
        start: Instant,
        end: Instant,
        description: String
    ): Booking

    fun updateStatus(booking: Booking, status: BookingStatus): Booking
    fun delete(booking: Booking)
    fun countByStatusAndDateRange(status: BookingStatus, start: LocalDateTime, end: LocalDateTime): Int
}

class BookingRepositoryImpl : BookingRepository {
    override fun findById(id: Int): Booking? = Booking.findById(id)

    override fun findByUserId(userId: Int): List<Booking> =
        Booking.find { Bookings.user eq userId }.with(Booking::user).toList()

    override fun findByRoomIdsAndDate(roomIds: List<Int>, date: LocalDate): List<Booking> {
        if (roomIds.isEmpty()) {
            return emptyList()
        }
        val startOfDay = LocalDateTime(date.year, date.month, date.dayOfMonth, 0, 0)
        val endOfDay = LocalDateTime(date.year, date.month, date.dayOfMonth, 23, 59, 59)
        return Booking.find {
            (Bookings.room inList roomIds) and (Bookings.start greaterEq startOfDay) and (Bookings.start lessEq endOfDay)
        }.with(Booking::user).toList()
    }

    override fun findOverlappingBookings(roomId: Int, start: Instant, end: Instant): List<Booking> {
        val startDateTime = start.toLocalDateTime(TimeZone.UTC)
        val endDateTime = end.toLocalDateTime(TimeZone.UTC)

        return Booking.find {
            (Bookings.room eq roomId) and
                    ((Bookings.status eq BookingStatus.RESERVED) or (Bookings.status eq BookingStatus.CHECKED_IN)) and
                    (Bookings.start less endDateTime) and
                    (Bookings.end greater startDateTime)
        }.toList()
    }

    override fun findExpiredReservations(dateTime: LocalDateTime): List<Booking> {
        return Booking.find {
            (Bookings.status eq BookingStatus.RESERVED) and (Bookings.start lessEq dateTime)
        }.filter { booking ->
            val expirationTime = booking.start.toInstant(TimeZone.UTC).plus(booking.gracePeriodMin, DateTimeUnit.MINUTE)
            expirationTime < dateTime.toInstant(TimeZone.UTC)
        }
    }

    override fun findBookingsCheckInWindow(dateTime: LocalDateTime): List<Booking> {
        return Booking.find {
            (Bookings.status eq BookingStatus.RESERVED) and (Bookings.end greater dateTime)
        }.filter { booking ->
            val checkInOpenTime = booking.start.toInstant(TimeZone.UTC).minus(booking.gracePeriodMin, DateTimeUnit.MINUTE)
            checkInOpenTime <= dateTime.toInstant(TimeZone.UTC)
        }
    }

    override fun create(
        user: User,
        room: Room,
        start: Instant,
        end: Instant,
        description: String,
        gracePeriodMin: Int,
        confirmationCode: String,
    ): Booking = Booking.new {
        this.start = start.toLocalDateTime(TimeZone.UTC)
        this.end = end.toLocalDateTime(TimeZone.UTC)
        this.createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        this.gracePeriodMin = gracePeriodMin
        this.status = BookingStatus.RESERVED
        this.description = description
        this.user = user
        this.room = room
        this.confirmationCode = confirmationCode
    }

    override fun update(
        booking: Booking,
        room: Room,
        start: Instant,
        end: Instant,
        description: String
    ): Booking {
        booking.start = start.toLocalDateTime(TimeZone.UTC)
        booking.end = end.toLocalDateTime(TimeZone.UTC)
        booking.description = description
        booking.room = room
        return booking
    }

    override fun updateStatus(booking: Booking, status: BookingStatus): Booking {
        booking.status = status
        return booking
    }

    override fun delete(booking: Booking) {
        booking.delete()
    }

    override fun countByStatusAndDateRange(
        status: BookingStatus,
        start: LocalDateTime,
        end: LocalDateTime
    ): Int {
        return Booking.find {
            (Bookings.status eq status) and (Bookings.start greaterEq start) and (Bookings.start lessEq end)
        }.count().toInt()
    }
}
