package com.judu.transport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judu.transport.data.model.Vehicle
import com.judu.transport.data.repository.RealtimeNetwork
import com.judu.transport.data.repository.RealtimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MapViewModel : ViewModel() {
    private val repository = RealtimeRepository(RealtimeNetwork.api)

    val vehicles: StateFlow<List<Vehicle>> = repository.getRealtimeVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
