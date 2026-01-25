package edu.mci.service

import edu.mci.model.api.request.CreateRoomRequest
import edu.mci.model.api.request.UpdateRoomRequest
import edu.mci.model.api.request.RoomEquipmentRequest
import edu.mci.model.api.response.EquipmentResponse
import edu.mci.model.api.response.RoomDeletionBlocker
import edu.mci.model.api.response.RoomDeletionConflictResponse
import edu.mci.model.api.response.RoomWithBookingsResponse
import edu.mci.model.api.response.RoomResponse
import edu.mci.model.db.BookingStatus
import edu.mci.model.db.EquipmentType
import edu.mci.model.db.Room
import edu.mci.model.db.RoomEquipmentItem
import edu.mci.model.db.RoomEquipmentItems
import edu.mci.model.db.RoomStatus
import edu.mci.model.db.toResponse
import edu.mci.repository.BookingRepository
import edu.mci.repository.BuildingRepository
import edu.mci.repository.EquipmentRepository
import edu.mci.repository.RoomRepository
import edu.mci.repository.SearchedItemRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class RoomService(
    private val roomRepository: RoomRepository,
    private val bookingRepository: BookingRepository,
    private val equipmentRepository: EquipmentRepository,
    private val searchedItemRepository: SearchedItemRepository,
    private val buildingRepository: BuildingRepository
) {
    fun getAllRooms(
        capacity: Int?,
        buildingId: Int?,
        date: LocalDate?,
        requiredEquipment: List<String>,
    ): List<RoomWithBookingsResponse> = transaction {
        if (requiredEquipment.isNotEmpty()) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            requiredEquipment.forEach {
                searchedItemRepository.recordSearch(it, now)
            }
        }
        val rooms = roomRepository.findAll(capacity, buildingId, requiredEquipment)

        val bookingsByRoom = if (date != null && rooms.isNotEmpty()) {
            bookingRepository.findByRoomIdsAndDate(rooms.map { it.id.value }, date)
                .filter { it.status == BookingStatus.RESERVED || it.status == BookingStatus.CHECKED_IN }
                .groupBy { it.room?.id?.value }
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

    fun createRoom(request: CreateRoomRequest): RoomResponse = transaction {
        validateRoomRequest(request.roomNumber, request.name, request.description, request.confirmationCode, request.capacity)
        val equipment = parseEquipmentRequests(request.equipment)
        val status = parseStatus(request.status)
        val building = buildingRepository.findById(request.buildingId)
            ?: throw BuildingNotFoundException("Building not found")

        val room = roomRepository.create(
            roomNumber = request.roomNumber,
            name = request.name,
            description = request.description,
            status = status,
            confirmationCode = request.confirmationCode,
            capacity = request.capacity,
            building = building
        )
        if (equipment.isNotEmpty()) {
            upsertRoomEquipment(room, equipment)
        }
        room.toResponse()
    }

    fun updateRoom(roomId: Int, request: UpdateRoomRequest): RoomResponse = transaction {
        validateRoomRequest(request.roomNumber, request.name, request.description, request.confirmationCode, request.capacity)
        val equipment = parseEquipmentRequests(request.equipment)
        val status = parseStatus(request.status)
        val room = roomRepository.findById(roomId) ?: throw RoomNotFoundException("Room not found")
        val building = buildingRepository.findById(request.buildingId)
            ?: throw BuildingNotFoundException("Building not found")

        roomRepository.update(
            room = room,
            roomNumber = request.roomNumber,
            name = request.name,
            description = request.description,
            status = status,
            confirmationCode = request.confirmationCode,
            capacity = request.capacity,
            building = building
        )
        if (equipment.isNotEmpty()) {
            upsertRoomEquipment(room, equipment)
        }
        room.toResponse()
    }

    fun deleteRoom(roomId: Int) = transaction {
        val room = roomRepository.findById(roomId) ?: throw RoomNotFoundException("Room not found")
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val activeBookingsCount = bookingRepository.countActiveByRoomId(roomId, now)
        if (activeBookingsCount > 0) {
            val blockers = buildList {
                add(
                    RoomDeletionBlocker(
                        type = "bookings",
                        count = activeBookingsCount,
                        suggestedAction = "Move or cancel active bookings before deleting the room"
                    )
                )
            }
            throw RoomDeletionBlockedException(
                RoomDeletionConflictResponse(
                    message = "Room deletion blocked by existing dependencies",
                    blockers = blockers
                )
            )
        }

        bookingRepository.clearRoomReferences(roomId)
        equipmentRepository.deleteByRoomId(roomId)
        roomRepository.delete(room)
    }

    private fun validateRoomRequest(
        roomNumber: Int,
        name: String,
        description: String,
        confirmationCode: String,
        capacity: Int
    ) {
        if (roomNumber <= 0) {
            throw RoomValidationException("Room number must be a positive integer")
        }
        if (name.isBlank()) {
            throw RoomValidationException("Room name is required")
        }
        if (description.isBlank()) {
            throw RoomValidationException("Room description is required")
        }
        if (confirmationCode.isBlank()) {
            throw RoomValidationException("Confirmation code is required")
        }
        if (capacity < 0) {
            throw RoomValidationException("Capacity must be zero or greater")
        }
    }

    private fun parseStatus(status: String): RoomStatus =
        RoomStatus.entries.firstOrNull { it.name == status }
            ?: throw RoomValidationException("Invalid room status: $status")

    private data class EquipmentSpec(
        val quantity: Int,
        val description: String?
    )

    private fun parseEquipmentRequests(requests: List<RoomEquipmentRequest>): Map<EquipmentType, EquipmentSpec> {
        if (requests.isEmpty()) {
            return emptyMap()
        }
        val equipment = mutableMapOf<EquipmentType, EquipmentSpec>()
        requests.forEach { request ->
            val type = EquipmentType.entries.firstOrNull { it.name == request.type }
                ?: throw RoomValidationException("Invalid equipment type: ${request.type}")
            if (request.quantity < 0) {
                throw RoomValidationException("Equipment quantity must be zero or greater for ${request.type}")
            }
            if (equipment.containsKey(type)) {
                throw RoomValidationException("Duplicate equipment type: ${request.type}")
            }
            val trimmedDescription = request.description?.trim()
            if (type == EquipmentType.OTHER && trimmedDescription.isNullOrEmpty()) {
                throw RoomValidationException("Description is required for equipment type OTHER")
            }
            equipment[type] = EquipmentSpec(
                quantity = request.quantity,
                description = trimmedDescription?.ifEmpty { null }
            )
        }
        return equipment
    }

    private fun upsertRoomEquipment(room: Room, equipment: Map<EquipmentType, EquipmentSpec>) {
        equipment.forEach { (type, spec) ->
            val existing = RoomEquipmentItem.find {
                (RoomEquipmentItems.room eq room.id) and (RoomEquipmentItems.type eq type)
            }.firstOrNull()
            if (spec.quantity == 0) {
                existing?.delete()
            } else if (existing != null) {
                existing.quantity = spec.quantity
                existing.description = spec.description
            } else {
                RoomEquipmentItem.new {
                    this.room = room
                    this.type = type
                    this.quantity = spec.quantity
                    this.description = spec.description
                }
            }
        }
    }
}

class RoomNotFoundException(message: String) : RuntimeException(message)

class BuildingNotFoundException(message: String) : RuntimeException(message)

class RoomValidationException(message: String) : RuntimeException(message)

class RoomDeletionBlockedException(val conflict: RoomDeletionConflictResponse) : RuntimeException(conflict.message)
