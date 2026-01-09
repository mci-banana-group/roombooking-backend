package edu.mci

import edu.mci.plugins.configureDatabases
import edu.mci.plugins.seedData
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    seedData()
    configureRouting()
}
