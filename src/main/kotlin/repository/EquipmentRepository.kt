package edu.mci.repository

import edu.mci.model.db.RoomEquipmentItem
import edu.mci.model.db.RoomEquipmentItems
import edu.mci.model.db.Rooms
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

interface EquipmentRepository {
    fun findAllForBuilding(buildingId: Int): List<RoomEquipmentItem>
    fun deleteByRoomId(roomId: Int)
}

class EquipmentRepositoryImpl : EquipmentRepository {
    override fun findAllForBuilding(buildingId: Int): List<RoomEquipmentItem> =
        RoomEquipmentItem.wrapRows(
            RoomEquipmentItems
                .innerJoin(Rooms)
                .select(RoomEquipmentItems.columns)
                .where { Rooms.building eq buildingId }
        ).toList()

    override fun deleteByRoomId(roomId: Int) {
        RoomEquipmentItems.deleteWhere { RoomEquipmentItems.room eq roomId }
    }
}