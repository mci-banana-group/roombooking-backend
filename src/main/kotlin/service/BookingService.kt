package edu.mci.service

import edu.mci.model.db.BookingStatus
import edu.mci.model.db.toResponse
import edu.mci.model.api.response.BookingResponse
import edu.mci.model.api.request.CheckInRequest
import edu.mci.model.api.request.CreateBookingRequest
import edu.mci.repository.BookingRepository
import edu.mci.repository.RoomRepository
import edu.mci.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction

class BookingService(
    private val bookingRepository: BookingRepository,
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
) {

    fun getBookingsForUser(userId: Int): List<BookingResponse> = transaction {
        bookingRepository.findByUserId(userId).map { it.toResponse() }
    }

    fun createBooking(userId: Int, createDto: CreateBookingRequest): BookingResponse = transaction {
        if (createDto.start >= createDto.end) {
            throw IllegalArgumentException("Start time must be before end time")
        }

        if (createDto.start <= kotlinx.datetime.Clock.System.now()) {
            throw IllegalArgumentException("Booking must be in the future")
        }

        val room = roomRepository.findById(createDto.roomId)
            ?: throw IllegalArgumentException("Room not found")

        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")

        val overlaps = bookingRepository.findOverlappingBookings(createDto.roomId, createDto.start, createDto.end)
        if (overlaps.isNotEmpty()) {
            throw IllegalStateException("Slot not available")
        }

        bookingRepository.create(
            user = user,
            room = room,
            start = createDto.start,
            end = createDto.end,
            description = createDto.description,
            gracePeriodMin = 15 // TODO fetch from config set by an admin
        ).toResponse()
    }

    fun updateBooking(userId: Int, bookingId: Int, updateDto: CreateBookingRequest): BookingResponse = transaction {
        val existingBooking =
            bookingRepository.findById(bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.user.id.value != userId) {
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
        ).toResponse()
    }

    fun deleteBooking(userId: Int, bookingId: Int) = transaction {
        val existingBooking =
            bookingRepository.findById(bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.user.id.value != userId) {
            throw IllegalAccessException("You are not authorized to delete this booking")
        }

        bookingRepository.delete(existingBooking)
    }

    fun checkIn(userId: Int, checkInRequest: CheckInRequest) = transaction {
        val existingBooking =
            bookingRepository.findById(checkInRequest.bookingId) ?: throw IllegalArgumentException("Booking not found")

        if (existingBooking.user.id.value != userId) {
            throw IllegalAccessException("You are not authorized to check in for this booking")
        }

        if (existingBooking.room.confirmationCode != checkInRequest.code) {
            throw IllegalArgumentException("Confirmation Code Not Matching")
        }

        bookingRepository.updateStatus(existingBooking, BookingStatus.CHECKED_IN)
    }
}
