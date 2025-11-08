package com.dbApp.approom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dbApp.approom.data.Compra
import com.dbApp.approom.data.CompraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CompraViewModel(private val repository: CompraRepository) : ViewModel() {

    private val _compras = MutableStateFlow<List<Compra>>(emptyList())
    val compras: StateFlow<List<Compra>> = _compras

    init {
        viewModelScope.launch {
            repository.allCompras.collect { comprasList ->
                _compras.value = comprasList
            }
        }
    }

    fun insertCompra(compra: Compra) {
        viewModelScope.launch {
            repository.insertCompra(compra)
        }
    }
}

class CompraViewModelFactory(private val repository: CompraRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

