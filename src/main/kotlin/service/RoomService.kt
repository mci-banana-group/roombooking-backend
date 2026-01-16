package edu.mci.service

import edu.mci.model.api.response.EquipmentResponse
import edu.mci.model.api.response.RoomWithBookingsResponse
import edu.mci.model.db.BookingStatus
import edu.mci.model.db.toResponse
import edu.mci.repository.BookingRepository
import edu.mci.repository.EquipmentRepository
import edu.mci.repository.RoomRepository
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.transactions.transaction

class RoomService(
    private val roomRepository: RoomRepository,
    private val bookingRepository: BookingRepository,
    private val equipmentRepository: EquipmentRepository,
) {
    fun getAllRooms(
        capacity: Int?,
        buildingId: Int?,
        date: LocalDate?,
        requiredEquipment: List<String>,
    ): List<RoomWithBookingsResponse> = transaction {
        val rooms = roomRepository.findAll(capacity, buildingId, requiredEquipment)

        val bookingsByRoom = if (date != null && rooms.isNotEmpty()) {
            bookingRepository.findByRoomIdsAndDate(rooms.map { it.id.value }, date)
                .filter { it.status == BookingStatus.RESERVED || it.status == BookingStatus.CHECKED_IN }
                .groupBy { it.room.id.value }
        } else {
            emptyMap()
        }

        rooms.map { room ->
            val bookings = bookingsByRoom[room.id.value]?.map { it.toResponse() } ?: emptyList()

            RoomWithBookingsResponse(
                room = room.toResponse(),
                bookings = bookings
            )
        }
    }

    fun getAllEquipmentForBuilding(buildingId: Int): List<EquipmentResponse> = transaction {
        equipmentRepository.findAllForBuilding(buildingId).map {
            it.toResponse()
        }
    }
}