package com.dbApp.approom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val comprador: String,
    val productos: String,
    val precioTotal: Double,
    val fecha: String
)

