package edu.mci.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

enum class RoomStatus {
    FREE, RESERVED, OCCUPIED
}

enum class EquipmentType {
    BEAMER, HDMI_CABLE, WHITEBOARD, DISPLAY
}

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

object RoomEquipmentItems : IntIdTable() {
    val quantity = integer("quantity")
    val type = enumerationByName("type", 20, EquipmentType::class)
    val room = reference("room_id", Rooms)
}

class RoomEquipmentItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoomEquipmentItem>(RoomEquipmentItems)

    var quantity by RoomEquipmentItems.quantity
    var type by RoomEquipmentItems.type
    var room by Room referencedOn RoomEquipmentItems.room
}
