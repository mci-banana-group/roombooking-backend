package edu.mci.model.db

import edu.mci.model.api.response.AdminRoomResponse
import edu.mci.model.api.response.RoomResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.and

object Rooms : IntIdTable() {
    val roomNumber = integer("room_number")
    val name = varchar("name", 100)
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

fun Room.refreshStatus() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    val nowInstant = now.toInstant(TimeZone.UTC)

    // 1. Is any booking currently CHECKED_IN and active? -> OCCUPIED
    val hasOccupied = Booking.find {
        (Bookings.room eq this@refreshStatus.id) and
                (Bookings.status eq BookingStatus.CHECKED_IN) and
                (Bookings.start lessEq now) and
                (Bookings.end greater now)
    }.any()

    if (hasOccupied) {
        this.status = RoomStatus.OCCUPIED
        return
    }

    // 2. Is any booking currently RESERVED and within its grace period? -> RESERVED
    val upcomingReservations = Booking.find {
        (Bookings.room eq this@refreshStatus.id) and
                (Bookings.status eq BookingStatus.RESERVED) and
                (Bookings.end greater now)
    }.toList()

    val isReserved = upcomingReservations.any { booking ->
        val start = booking.start.toInstant(TimeZone.UTC)
        val graceStart = start.minus(booking.gracePeriodMin, DateTimeUnit.MINUTE)
        val graceEnd = start.plus(booking.gracePeriodMin, DateTimeUnit.MINUTE)
        nowInstant in graceStart..graceEnd
    }

    this.status = if (isReserved) RoomStatus.RESERVED else RoomStatus.FREE
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
