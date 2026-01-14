package edu.mci.service

import edu.mci.model.api.response.BuildingResponse
import edu.mci.model.db.toResponse
import edu.mci.repository.BuildingRepository
import org.jetbrains.exposed.sql.transactions.transaction

class BuildingService(private val buildingRepository: BuildingRepository) {

    fun getALlBuildings(): List<BuildingResponse> = transaction {
        buildingRepository.findAll().map {
            it.toResponse()
        }
    }

}