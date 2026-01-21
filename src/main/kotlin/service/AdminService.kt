package edu.mci.service

import edu.mci.model.api.response.AdminDashboardResponse
import edu.mci.model.db.BookingStatus
import edu.mci.repository.BookingRepository
import edu.mci.repository.SearchedItemRepository
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction

class AdminService(
    private val bookingRepository: BookingRepository,
    private val searchedItemRepository: SearchedItemRepository
) {
    fun getDashboardStats(start: LocalDateTime, end: LocalDateTime, limit: Int): AdminDashboardResponse = transaction {
        val totalMeetings = bookingRepository.countByStatusAndDateRange(BookingStatus.CHECKED_IN, start, end)
        val cancelledMeetings = bookingRepository.countByStatusAndDateRange(BookingStatus.CANCELLED, start, end)
        val noShowMeetings = bookingRepository.countByStatusAndDateRange(BookingStatus.NO_SHOW, start, end)
        val reservedMeetings = bookingRepository.countByStatusAndDateRange(BookingStatus.RESERVED, start, end)
        val mostSearchedItems = searchedItemRepository.getMostSearchedItems(start, end, limit = limit)

        AdminDashboardResponse(
            totalMeetings = totalMeetings,
            cancelledMeetings = cancelledMeetings,
            noShowMeetings = noShowMeetings,
            reservedMeetings = reservedMeetings,
            mostSearchedItems = mostSearchedItems
        )
    }
}
