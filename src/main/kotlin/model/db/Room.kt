package edu.mci.model.db

import edu.mci.model.api.response.EquipmentResponse
import edu.mci.model.api.response.RoomResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Rooms : IntIdTable() {
    val roomNumber = integer("room_number")
    val name = varchar("name", 100)
    val description = varchar("description", 255)
    val status = enumerationByName("status", 20, RoomStatus::class)
    val confirmationCode = varchar("confirmation_code", 50)
    val capacity = integer("capacity")
    val building = reference("building_id", Buildings)
}

class Room(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Room>(Rooms)

    var roomNumber by Rooms.roomNumber
    var name by Rooms.name
    var description by Rooms.description
    var status by Rooms.status
    var confirmationCode by Rooms.confirmationCode
    var capacity by Rooms.capacity
    var building by Building referencedOn Rooms.building
    val equipment by RoomEquipmentItem referrersOn RoomEquipmentItems.room
}

enum class RoomStatus {
    FREE, RESERVED, OCCUPIED
}

fun Room.toResponse() = RoomResponse(
    id = this.id.value,
    roomNumber = this.roomNumber,
    name = this.name,
    description = this.description,
    capacity = this.capacity,
    equipment = this.equipment.map {
        EquipmentResponse(
            id = it.id.value,
            name = it.type.name,
            quantity = it.quantity,
        )
    }
)
