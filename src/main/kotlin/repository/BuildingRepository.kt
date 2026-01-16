package edu.mci.repository

import edu.mci.model.db.Building
import edu.mci.model.db.Buildings
import org.jetbrains.exposed.sql.selectAll

interface BuildingRepository {
    fun findAll(): List<Building>
}

class BuildingRepositoryImpl : BuildingRepository {
    override fun findAll(): List<Building> = Building.wrapRows(Buildings.selectAll()).toList()

}