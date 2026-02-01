package edu.mci.service

import edu.mci.model.db.*
import edu.mci.repository.BookingRepository
import kotlinx.coroutines.*
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.minutes
import org.jetbrains.exposed.sql.and

class BookingScheduler(
    private val bookingRepository: BookingRepository,
    private val mqttService: MqttService
) {
    private val logger = LoggerFactory.getLogger(BookingScheduler::class.java)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun start() {
        logger.info("Starting Booking Scheduler")
        scope.launch {
            while (isActive) {
                try {
                    checkExpiredBookings()
                    checkCompletedBookings()
                    verifyCheckInWindow()
                } catch (e: Exception) {
                    logger.error("Error checking expired bookings", e)
                }
                delay(5.minutes)
            }
        }
    }

    private fun checkExpiredBookings() {
        transaction {
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val expiredBookings = bookingRepository.findExpiredReservations(now)
            if (expiredBookings.isNotEmpty()) {
                logger.info("Found ${expiredBookings.size} expired bookings")
                expiredBookings.forEach { booking ->
                    logger.info("Marking booking ${booking.id} as NO_SHOW")
                    bookingRepository.updateStatus(booking, BookingStatus.NO_SHOW)
                    booking.room?.refreshStatus()
                }
            }
        }
    }

    private fun checkCompletedBookings() {
        transaction {
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val completedBookings = bookingRepository.findCompletedBookings(now)
            if (completedBookings.isNotEmpty()) {
                logger.info("Found ${completedBookings.size} completed bookings")
                completedBookings.forEach { booking ->
                    logger.info("Marking booking ${booking.id} as COMPLETED")
                    bookingRepository.updateStatus(booking, BookingStatus.COMPLETED)
                    
                    val room = booking.room
                    if (room != null) {
                        logger.info("Triggering MQTT off/lock messages for room ${room.id.value}")
                        mqttService.publishLockDoor(room.id.value)
                        mqttService.publishTurnOffLight(room.id.value)
                        mqttService.publishTurnOffHVAC(room.id.value)
                        room.refreshStatus()
                    }
                }
            }
        }
    }

    private fun verifyCheckInWindow() {
        transaction {
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val bookings = bookingRepository.findBookingsCheckInWindow(now)
            if (bookings.isNotEmpty()) {
                logger.info("Found ${bookings.size} bookings in check-in window")
                bookings.forEach { booking ->
                    val room = booking.room
                    if (room == null) {
                        logger.warn("Skipping booking ${booking.id}: room no longer exists")
                        return@forEach
                    }
                    logger.info("Publishing code for booking ${booking.id} in room ${room.roomNumber}")
                    mqttService.publishRoomCode(room.id.value, booking.confirmationCode)
                    room.refreshStatus()
                }
            }
        }
    }
}
