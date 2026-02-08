package edu.mci.repository

import edu.mci.model.api.response.SearchedItemCount
import edu.mci.model.db.SearchedItem
import edu.mci.model.db.SearchedItems
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count

interface SearchedItemRepository {
    fun recordSearch(term: String, at: LocalDateTime)
    fun getMostSearchedItems(start: LocalDateTime, end: LocalDateTime, limit: Int): List<SearchedItemCount>
}

class SearchedItemRepositoryImpl : SearchedItemRepository {
    override fun recordSearch(term: String, at: LocalDateTime) {
        SearchedItem.new {
            this.searchTerm = term
            this.searchedAt = at
        }
    }

    override fun getMostSearchedItems(start: LocalDateTime, end: LocalDateTime, limit: Int): List<SearchedItemCount> {
        return SearchedItems
            .select(SearchedItems.searchTerm, SearchedItems.id.count())
            .where { (SearchedItems.searchedAt greaterEq start) and (SearchedItems.searchedAt lessEq end) }
            .groupBy(SearchedItems.searchTerm)
            .orderBy(SearchedItems.id.count(), SortOrder.DESC)
            .limit(limit)
            .map {
                SearchedItemCount(
                    term = it[SearchedItems.searchTerm],
                    count = it[SearchedItems.id.count()]
                )
            }
    }
}
