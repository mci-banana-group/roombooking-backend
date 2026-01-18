package edu.mci.service

import edu.mci.model.db.BookingStatus
import edu.mci.repository.BookingRepository
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.minutes

class BookingScheduler(
    private val bookingRepository: BookingRepository
) {
    private val logger = LoggerFactory.getLogger(BookingScheduler::class.java)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun start() {
        logger.info("Starting Booking Scheduler")
        scope.launch {
            while (isActive) {
                delay(5.minutes)
                try {
                    checkExpiredBookings()
                } catch (e: Exception) {
                    logger.error("Error checking expired bookings", e)
                }
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
                }
            }
        }
    }
}
