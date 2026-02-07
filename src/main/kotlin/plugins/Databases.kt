package edu.mci.plugins

import edu.mci.model.db.*
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val config = environment.config
    val dbMode = config.propertyOrNull("db.mode")?.getString()?.lowercase() ?: "inmemory"

    val database = if (dbMode == "inmemory") {
        // Start H2 Web Console if needed:
        org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()
        Database.connect(
            url = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    } else {
        val url = config.property("db.url").getString()
        val user = config.property("db.user").getString()
        val password = config.property("db.password").getString()
        val driver = config.property("db.driver").getString()

        Database.connect(
            url = url,
            user = user,
            password = password,
            driver = driver
        )
    }
    transaction(database) {
        SchemaUtils.create(
            Users,
            Buildings,
            Rooms,
            RoomEquipmentItems,
            Bookings,
            PresenceConfirmations,
            Notifications,
            SearchedItems
        )
    }
}
