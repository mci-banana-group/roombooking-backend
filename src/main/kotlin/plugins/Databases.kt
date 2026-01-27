package edu.mci.plugins

import edu.mci.model.db.Bookings
import edu.mci.model.db.Buildings
import edu.mci.model.db.Notifications
import edu.mci.model.db.SearchedItems
import edu.mci.model.db.PresenceConfirmations
import edu.mci.model.db.RoomEquipmentItems
import edu.mci.model.db.Rooms
import edu.mci.model.db.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    /* Keeping H2 for Testing purposes
    val database = Database.connect(
          url = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
          user = "root",
          driver = "org.h2.Driver",
          password = ""

       */
    val config = environment.config
    val url = config.property("db.url").getString()
    val user = config.property("db.user").getString()
    val password = config.property("db.password").getString()
    val driver = config.property("db.driver").getString()

    val database = Database.connect(
        url = url,
        user = user,
        password = password,
        driver = driver
    )

    // Start H2 Web Console
    // org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()

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
