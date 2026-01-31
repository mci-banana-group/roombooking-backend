package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class AdminDashboardResponse(
    val totalMeetings: Map<String, Int>,
    val userCancelledMeetings: Map<String, Int>,
    val adminCancelledMeetings: Map<String, Int>,
    val completedBookings: Map<String, Int>,
    val checkedInBookings: Map<String, Int>,
    val noShowMeetings: Map<String, Int>,
    val reservedMeetings: Map<String, Int>,
    val mostSearchedItems: List<SearchedItemCount>
)

@Serializable
data class SearchedItemCount(
    val term: String,
    val count: Long
)
