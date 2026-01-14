package edu.mci.repository

import edu.mci.model.db.Room

interface RoomRepository {
    fun findById(id: Int): Room?
}

class RoomRepositoryImpl : RoomRepository {
    override fun findById(id: Int): Room? = Room.findById(id)
}
