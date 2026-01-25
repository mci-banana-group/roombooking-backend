package edu.mci.repository

import edu.mci.model.db.Building
import edu.mci.model.db.EquipmentType
import edu.mci.model.db.Room
import edu.mci.model.db.RoomEquipmentItems
import edu.mci.model.db.RoomStatus
import edu.mci.model.db.Rooms
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll

interface RoomRepository {
    fun findById(id: Int): Room?
    fun findAll(capacity: Int?, buildingId: Int?, requiredEquipment: List<String>): List<Room>
    fun create(
        roomNumber: Int,
        name: String,
        description: String,
        status: RoomStatus,
        confirmationCode: String,
        capacity: Int,
        building: Building
    ): Room

    fun update(
        room: Room,
        roomNumber: Int,
        name: String,
        description: String,
        status: RoomStatus,
        confirmationCode: String,
        capacity: Int,
        building: Building
    ): Room

    fun delete(room: Room)
}

class RoomRepositoryImpl : RoomRepository {
    override fun findById(id: Int): Room? = Room.findById(id)

    override fun findAll(capacity: Int?, buildingId: Int?, requiredEquipment: List<String>): List<Room> {
        val equipmentTypes = requiredEquipment.mapNotNull { required ->
            runCatching {
                EquipmentType.valueOf(required)
            }.getOrNull()
        }

        val query = if (equipmentTypes.isNotEmpty()) {
            Rooms.innerJoin(RoomEquipmentItems)
                .select(Rooms.columns)
                .where { RoomEquipmentItems.type inList equipmentTypes }
                .groupBy(Rooms.id)
                .having { RoomEquipmentItems.type.count() eq equipmentTypes.size.toLong() }
        } else {
            Rooms.selectAll()
        }

        capacity?.let { query.andWhere { Rooms.capacity greaterEq it } }
        buildingId?.let { query.andWhere { Rooms.building eq it } }

        return Room.wrapRows(query).with(Room::equipment).toList()
    }

    override fun create(
        roomNumber: Int,
        name: String,
        description: String,
        status: RoomStatus,
        confirmationCode: String,
        capacity: Int,
        building: Building
    ): Room = Room.new {
        this.roomNumber = roomNumber
        this.name = name
        this.description = description
        this.status = status
        this.confirmationCode = confirmationCode
        this.capacity = capacity
        this.building = building
    }

    override fun update(
        room: Room,
        roomNumber: Int,
        name: String,
        description: String,
        status: RoomStatus,
        confirmationCode: String,
        capacity: Int,
        building: Building
    ): Room {
        room.roomNumber = roomNumber
        room.name = name
        room.description = description
        room.status = status
        room.confirmationCode = confirmationCode
        room.capacity = capacity
        room.building = building
        return room
    }

    override fun delete(room: Room) {
        room.delete()
    }
}
