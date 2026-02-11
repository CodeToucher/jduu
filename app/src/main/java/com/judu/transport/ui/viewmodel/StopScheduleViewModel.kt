package com.judu.transport.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judu.transport.data.db.AppDatabase
import com.judu.transport.data.db.entities.StopTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StopScheduleViewModel(
    application: Application,
    private val routeId: String,
    private val stopId: String,
    private val stopName: String
) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    
    private val _schedule = MutableStateFlow<List<StopTime>>(emptyList())
    val schedule: StateFlow<List<StopTime>> = _schedule
    
    val stopNameState = stopName

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            // TODO: Filter by direction_id if needed, currently seeing all.
            // Ideally we pass directionId too.
            _schedule.value = database.stopTimeDao().getScheduleForRouteAndStop(routeId, stopId)
        }
    }
}

class StopScheduleViewModelFactory(
    private val application: Application,
    private val routeId: String,
    private val stopId: String,
    private val stopName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StopScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StopScheduleViewModel(application, routeId, stopId, stopName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
