package edu.mci.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

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

enum class EquipmentType {
    BEAMER, HDMI_CABLE, WHITEBOARD, DISPLAY
}
