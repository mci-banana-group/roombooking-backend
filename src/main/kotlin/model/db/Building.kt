package edu.mci.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Buildings : IntIdTable() {
    val name = varchar("name", 100)
    val address = varchar("address", 255)
}

class Building(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Building>(Buildings)

    var name by Buildings.name
    var address by Buildings.address
    val rooms by Room referrersOn Rooms.building
}
