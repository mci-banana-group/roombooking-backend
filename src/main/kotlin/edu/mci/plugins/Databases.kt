package edu.mci.plugins

import edu.mci.models.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
        user = "root",
        driver = "org.h2.Driver",
        password = ""
    )
    
    transaction(database) {
        SchemaUtils.create(
            Users,
            Buildings,
            Rooms,
            RoomEquipmentItems,
            Bookings,
            PresenceConfirmations,
            Notifications
        )
    }
}
