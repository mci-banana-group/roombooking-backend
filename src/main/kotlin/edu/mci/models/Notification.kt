package edu.mci.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

enum class NotificationChannel {
    EMAIL, PUSH
}

object Notifications : IntIdTable() {
    val sentAt = datetime("sent_at")
    val message = varchar("message", 255)
    val channel = enumerationByName("channel", 20, NotificationChannel::class)
    
    val user = reference("user_id", Users)
    val booking = reference("booking_id", Bookings)
}

class Notification(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Notification>(Notifications)

    var sentAt by Notifications.sentAt
    var message by Notifications.message
    var channel by Notifications.channel
    
    var user by User referencedOn Notifications.user
    var booking by Booking referencedOn Notifications.booking
}
