package edu.mci.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object SearchedItems : IntIdTable() {
    val searchTerm = varchar("search_term", 100)
    val searchedAt = datetime("searched_at")
}

class SearchedItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SearchedItem>(SearchedItems)

    var searchTerm by SearchedItems.searchTerm
    var searchedAt by SearchedItems.searchedAt
}
