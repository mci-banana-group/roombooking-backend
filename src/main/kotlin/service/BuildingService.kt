package edu.mci.service

import edu.mci.model.api.response.BuildingResponse
import edu.mci.model.api.response.BuildingDeletionConflictResponse
import edu.mci.model.db.toResponse
import edu.mci.repository.BuildingRepository
import edu.mci.repository.RoomRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

class BuildingService(
    private val buildingRepository: BuildingRepository,
    private val roomRepository: RoomRepository
) {

    fun getAllBuildings(): List<BuildingResponse> = transaction {
        buildingRepository.findAll().map {
            it.toResponse()
        }
    }

    fun createBuilding(name: String, address: String): BuildingResponse = transaction {
        validateBuilding(name, address)
        try {
            buildingRepository.create(name.trim(), address.trim()).toResponse()
        } catch (e: ExposedSQLException) {
            if (e.cause.isUniqueConstraintViolation()) {
                throw BuildingConflictException("Building already exists")
            }
            throw e
        }
    }

    fun updateBuilding(buildingId: Int, name: String, address: String): BuildingResponse = transaction {
        validateBuilding(name, address)
        val building = buildingRepository.findById(buildingId) ?: throw BuildingNotFoundException("Building not found")
        try {
            buildingRepository.update(building, name.trim(), address.trim()).toResponse()
        } catch (e: ExposedSQLException) {
            if (e.cause.isUniqueConstraintViolation()) {
                throw BuildingConflictException("Building already exists")
            }
            throw e
        }
    }

    fun deleteBuilding(buildingId: Int) = transaction {
        val building = buildingRepository.findById(buildingId) ?: throw BuildingNotFoundException("Building not found")
        val roomsCount = roomRepository.countByBuildingId(buildingId)
        if (roomsCount > 0) {
            throw BuildingDeletionBlockedException(
                BuildingDeletionConflictResponse(
                    message = "Building deletion blocked by existing rooms",
                    roomsCount = roomsCount
                )
            )
        }
        buildingRepository.delete(building)
    }

    private fun validateBuilding(name: String, address: String) {
        if (name.isBlank()) {
            throw BuildingValidationException("Building name is required")
        }
        if (address.isBlank()) {
            throw BuildingValidationException("Building address is required")
        }
    }
}

class BuildingValidationException(message: String) : RuntimeException(message)

class BuildingConflictException(message: String) : RuntimeException(message)

class BuildingDeletionBlockedException(val conflict: BuildingDeletionConflictResponse) : RuntimeException(conflict.message)

private fun Throwable?.isUniqueConstraintViolation(): Boolean {
    val sqlException = this as? SQLException ?: return false
    return sqlException.sqlState == "23505"
}
