package edu.mci.service

import edu.mci.model.db.*
import edu.mci.model.db.toResponse
import edu.mci.model.api.response.BookingResponse
import edu.mci.model.api.request.CheckInRequest
import edu.mci.model.api.request.CreateBookingRequest
import edu.mci.model.api.response.AdminUserBookingResponse
import edu.mci.repository.BookingRepository
import edu.mci.repository.RoomRepository
import edu.mci.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.Instant

class BookingService(
    private val bookingRepository: BookingRepository,
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val mqttService: MqttService,
) {
    fun getBookingsForUser(userId: Int): List<BookingResponse> = transaction {
        bookingRepository.findByUserId(userId).map { it.toResponse() }
    }

    fun getBookingsForUserAdmin(userId: Int, start: Instant?, end: Instant?): List<AdminUserBookingResponse> = transaction {
        bookingRepository.findBookingsByUserId(userId, start, end).map { booking ->
            AdminUserBookingResponse(
                id = booking.id.value,
                roomId = booking.room?.id?.value,
                roomName = booking.room?.name,
                userId = booking.user?.id?.value,
                startTime = booking.start.toInstant(TimeZone.UTC),
                endTime = booking.end.toInstant(TimeZone.UTC),
                status = booking.status.name,
                description = booking.description
            )
        }
    }

    fun getBookingsForRoom(roomId: Int, start: Instant, end: Instant?, limit: Int?): List<BookingResponse> = transaction {
        bookingRepository.findBookingsByRoomId(roomId, start, end, limit).map { it.toResponse() }
    }

    fun createBooking(userId: Int, createDto: CreateBookingRequest): BookingResponse = transaction {
        if (createDto.start >= createDto.end) {
            throw IllegalArgumentException("Start time must be before end time")
        }

        if (createDto.start <= now()) {
            throw IllegalArgumentException("Booking must be in the future")
        }

        val room = roomRepository.findById(createDto.roomId)
            ?: throw IllegalArgumentException("Room not found")

        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")

        val overlaps = bookingRepository.findOverlappingBookings(createDto.roomId, createDto.start, createDto.end)
        if (overlaps.isNotEmpty()) {
            throw IllegalStateException("Slot not available")
        }

        val booking = bookingRepository.create(
            user = user,
            room = room,
            start = createDto.start,
            end = createDto.end,
            description = createDto.description,
            gracePeriodMin = 15, // TODO fetch from config set by an admin
            confirmationCode = (1000..9999).random().toString()
        )
        room.refreshStatus()
        booking.toResponse()
    }

    fun updateBooking(userId: Int, bookingId: Int, updateDto: CreateBookingRequest): BookingResponse = transaction {
        val existingBooking =
            bookingRepository.findById(bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.user?.id?.value != userId) {
            throw IllegalAccessException("You are not authorized to update this booking")
        }

        val room = roomRepository.findById(updateDto.roomId)
            ?: throw IllegalArgumentException("Room ${updateDto.roomId} not found")

        bookingRepository.update(
            booking = existingBooking,
            room = room,
            start = updateDto.start,
            end = updateDto.end,
            description = updateDto.description
        )
        room.refreshStatus()
        existingBooking.toResponse()
    }

    fun adminCancelBooking(bookingId: Int) = transaction {
        val existingBooking =
            bookingRepository.findById(bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.end.toInstant(TimeZone.UTC) <= now()) {
            throw IllegalStateException("Booking is in the past and cannot be cancelled")
        }

        if (existingBooking.status == BookingStatus.CANCELLED || existingBooking.status == BookingStatus.ADMIN_CANCELLED) {
            throw IllegalStateException("Booking is already cancelled")
        }

        bookingRepository.updateStatus(existingBooking, BookingStatus.ADMIN_CANCELLED)
        existingBooking.room?.refreshStatus()
    }

    fun cancelBooking(userId: Int, bookingId: Int) = transaction {
        val existingBooking =
            bookingRepository.findById(bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.user?.id?.value != userId) {
            throw IllegalAccessException("You are not authorized to cancel this booking")
        }

        if (existingBooking.status == BookingStatus.CANCELLED) {
            throw IllegalStateException("Booking is already cancelled")
        }

        bookingRepository.updateStatus(existingBooking, BookingStatus.CANCELLED)
        existingBooking.room?.refreshStatus()
    }

    fun checkIn(userId: Int, checkInRequest: CheckInRequest) = transaction {
        val existingBooking =
            bookingRepository.findById(checkInRequest.bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.user?.id?.value != userId) {
            throw IllegalAccessException("You are not authorized to check in for this booking")
        }

        val room = existingBooking.room ?: throw IllegalArgumentException("Room not found")
        val roomConfirmationCode = room.confirmationCode

        // Either booking confirmation code (dynamic) or room confirmation code (static fallback) must match
        if (existingBooking.confirmationCode != checkInRequest.code && roomConfirmationCode != checkInRequest.code) {
            throw IllegalArgumentException("Confirmation Code Not Matching")
        }

        bookingRepository.updateStatus(existingBooking, BookingStatus.CHECKED_IN)
        room.status = RoomStatus.OCCUPIED

        val roomId = room.id.value
        mqttService.publishUnlockDoor(roomId)
        mqttService.publishTurnOnLight(roomId)
        mqttService.publishTurnOnHVAC(roomId)
    }
}
