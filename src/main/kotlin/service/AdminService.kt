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
        val totalMeetings =
            bookingRepository.countAllByDateRangeByDay(start, end).mapKeys {
                it.toString()
            }

        val userCancelledMeetings =
            bookingRepository.countByStatusAndDateRangeByDay(BookingStatus.CANCELLED, start, end).mapKeys {
                it.toString()
            }

        val adminCancelledMeetings =
            bookingRepository.countByStatusAndDateRangeByDay(BookingStatus.ADMIN_CANCELLED, start, end).mapKeys {
                it.toString()
            }

        val completedBookings =
            bookingRepository.countByStatusAndDateRangeByDay(BookingStatus.COMPLETED, start, end).mapKeys {
                it.toString()
            }

        val checkedInBookings =
            bookingRepository.countByStatusAndDateRangeByDay(BookingStatus.CHECKED_IN, start, end).mapKeys {
                it.toString()
            }

        val noShowMeetings =
            bookingRepository.countByStatusAndDateRangeByDay(BookingStatus.NO_SHOW, start, end).mapKeys {
                it.toString()
            }

        val reservedMeetings =
            bookingRepository.countByStatusAndDateRangeByDay(BookingStatus.RESERVED, start, end).mapKeys {
                it.toString()
            }

        val mostSearchedItems = searchedItemRepository.getMostSearchedItems(start, end, limit = limit)

        AdminDashboardResponse(
            totalMeetings = totalMeetings,
            userCancelledMeetings = userCancelledMeetings,
            adminCancelledMeetings = adminCancelledMeetings,
            completedBookings = completedBookings,
            checkedInBookings = checkedInBookings,
            noShowMeetings = noShowMeetings,
            reservedMeetings = reservedMeetings,
            mostSearchedItems = mostSearchedItems
        )
    }
}
