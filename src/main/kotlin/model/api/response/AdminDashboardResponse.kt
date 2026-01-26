package edu.mci.model.api.response

import kotlinx.serialization.Serializable

@Serializable
data class AdminDashboardResponse(
    val totalMeetings: Int,
    val cancelledMeetings: Int,
    val noShowMeetings: Int,
    val reservedMeetings: Int,
    val mostSearchedItems: List<SearchedItemCount>
)

@Serializable
data class SearchedItemCount(
    val term: String,
    val count: Long
)
