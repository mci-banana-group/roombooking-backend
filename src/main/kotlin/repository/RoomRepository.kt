package edu.mci.repository

import edu.mci.model.db.Room
import edu.mci.model.db.Rooms
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll

interface RoomRepository {
    fun findById(id: Int): Room?
    fun findAll(capacity: Int?, buildingId: Int?): List<Room>
}

class RoomRepositoryImpl : RoomRepository {
    override fun findById(id: Int): Room? = Room.findById(id)

    override fun findAll(capacity: Int?, buildingId: Int?): List<Room> {
        val query = Rooms.selectAll()
        capacity?.let { query.andWhere { Rooms.capacity greaterEq it } }
        buildingId?.let { query.andWhere { Rooms.building eq it } }
        return Room.wrapRows(query).toList()
    }
}
