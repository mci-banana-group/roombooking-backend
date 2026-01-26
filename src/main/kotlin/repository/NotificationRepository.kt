package edu.mci.repository

import edu.mci.model.db.Notifications
import org.jetbrains.exposed.sql.update

interface NotificationRepository {
    fun clearUserReferences(userId: Int)
}

class NotificationRepositoryImpl : NotificationRepository {
    override fun clearUserReferences(userId: Int) {
        Notifications.update({ Notifications.user eq userId }) {
            it[user] = null
        }
    }
}
