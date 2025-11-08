package com.dbApp.approom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {
    @Query("SELECT * FROM compras ORDER BY id DESC")
    fun getAllCompras(): Flow<List<Compra>>

    @Insert
    suspend fun insertCompra(compra: Compra)
}

