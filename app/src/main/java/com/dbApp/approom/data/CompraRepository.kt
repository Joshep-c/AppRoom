package com.dbApp.approom.data

import kotlinx.coroutines.flow.Flow

class CompraRepository(private val compraDao: CompraDao) {
    val allCompras: Flow<List<Compra>> = compraDao.getAllCompras()

    suspend fun insertCompra(compra: Compra) {
        compraDao.insertCompra(compra)
    }
}

