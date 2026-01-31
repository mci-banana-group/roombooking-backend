package edu.mci.model.db

import edu.mci.model.api.response.AdminRoomResponse
import edu.mci.model.api.response.RoomResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Rooms : IntIdTable() {
    val roomNumber = integer("room_number")
    val name = varchar("name", 20)
    val description = varchar("description", 255)
    val status = enumerationByName("status", 20, RoomStatus::class)
    val confirmationCode = varchar("confirmation_code", 50)
    val capacity = integer("capacity")
    val building = reference("building_id", Buildings).nullable()
}

class Room(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Room>(Rooms)

    var roomNumber by Rooms.roomNumber
    var name by Rooms.name
    var description by Rooms.description
    var status by Rooms.status
    var confirmationCode by Rooms.confirmationCode
    var capacity by Rooms.capacity
    var building by Building optionalReferencedOn Rooms.building
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
    status = this.status.name,
    capacity = this.capacity,
    equipment = this.equipment.map {
        it.toResponse()
    },
    building = this.building?.toResponse()
)

fun Room.toAdminResponse() = AdminRoomResponse(
    id = this.id.value,
    roomNumber = this.roomNumber,
    name = this.name,
    description = this.description,
    status = this.status.name,
    capacity = this.capacity,
    confirmationCode = this.confirmationCode,
    equipment = this.equipment.map {
        it.toResponse()
    },
    building = this.building?.toResponse()
)
