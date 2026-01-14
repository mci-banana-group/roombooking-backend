package edu.mci.model.db

import edu.mci.model.api.response.BookingResponse
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Bookings : IntIdTable() {
    val start = datetime("start")
    val end = datetime("end")
    val createdAt = datetime("created_at")
    val gracePeriodMin = integer("grace_period_min")
    val status = enumerationByName("status", 20, BookingStatus::class)
    val description = varchar("description", 255)

    val user = reference("user_id", Users)
    val room = reference("room_id", Rooms)
}

class Booking(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Booking>(Bookings)

    var start by Bookings.start
    var end by Bookings.end
    var createdAt by Bookings.createdAt
    var gracePeriodMin by Bookings.gracePeriodMin
    var status by Bookings.status
    var description by Bookings.description

    var user by User referencedOn Bookings.user
    var room by Room referencedOn Bookings.room
    val confirmations by PresenceConfirmation referrersOn PresenceConfirmations.booking
    val notifications by Notification referrersOn Notifications.booking
}

enum class BookingStatus {
    RESERVED, CANCELLED, CHECKED_IN, NO_SHOW
}

fun Booking.toResponse() = BookingResponse(
    id = this.id.value,
    user = this.user.toResponse(),
    start = this.start.toInstant(TimeZone.UTC),
    end = this.end.toInstant(TimeZone.UTC),
    gracePeriodMin = this.gracePeriodMin,
    description = this.description,
)
