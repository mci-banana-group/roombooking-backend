package edu.mci.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PresenceConfirmations : IntIdTable() {
    val timestamp = datetime("timestamp")
    val method = enumerationByName("method", 20, ConfirmationMethod::class)
    val booking = reference("booking_id", Bookings)
}

class PresenceConfirmation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PresenceConfirmation>(PresenceConfirmations)

    var timestamp by PresenceConfirmations.timestamp
    var method by PresenceConfirmations.method
    var booking by Booking referencedOn PresenceConfirmations.booking
}

enum class ConfirmationMethod {
    QR_CODE, NFC, MOTION
}
