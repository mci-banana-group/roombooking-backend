package edu.mci.repository

import edu.mci.model.db.Building
import edu.mci.model.db.Buildings
import org.jetbrains.exposed.sql.selectAll

interface BuildingRepository {
    fun findAll(): List<Building>
    fun findById(id: Int): Building?
    fun create(name: String, address: String): Building
    fun update(building: Building, name: String, address: String): Building
    fun delete(building: Building)
}

class BuildingRepositoryImpl : BuildingRepository {
    override fun findAll(): List<Building> = Building.wrapRows(Buildings.selectAll()).toList()

    override fun findById(id: Int): Building? = Building.findById(id)

    override fun create(name: String, address: String): Building = Building.new {
        this.name = name
        this.address = address
    }

    override fun update(building: Building, name: String, address: String): Building {
        building.name = name
        building.address = address
        return building
    }

    override fun delete(building: Building) {
        building.delete()
    }
}
