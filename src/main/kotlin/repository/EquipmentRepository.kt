package edu.mci.repository

import edu.mci.model.db.RoomEquipmentItem
import edu.mci.model.db.RoomEquipmentItems
import edu.mci.model.db.Rooms

interface EquipmentRepository {
    fun findAllForBuilding(buildingId: Int): List<RoomEquipmentItem>
}

class EquipmentRepositoryImpl : EquipmentRepository {
    override fun findAllForBuilding(buildingId: Int): List<RoomEquipmentItem> =
        RoomEquipmentItem.wrapRows(
            RoomEquipmentItems
                .innerJoin(Rooms)
                .select(RoomEquipmentItems.columns)
                .where { Rooms.building eq buildingId }
        ).toList()
}