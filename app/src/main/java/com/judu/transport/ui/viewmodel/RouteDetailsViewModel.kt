package com.judu.transport.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judu.transport.data.db.AppDatabase
import com.judu.transport.data.db.entities.Route
import com.judu.transport.data.db.entities.Stop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RouteDirection(
    val headsign: String,
    val stops: List<Stop>
)

class RouteDetailsViewModel(application: Application, private val routeId: String) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    private val _route = MutableStateFlow<Route?>(null)
    val route: StateFlow<Route?> = _route
    
    private val _directions = MutableStateFlow<List<RouteDirection>>(emptyList())
    val directions: StateFlow<List<RouteDirection>> = _directions

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _route.value = database.routeDao().getRoute(routeId)
            
            val trips = database.tripDao().getRepresentativeTrips(routeId)
            val dirs = trips.map { trip ->
                val stops = database.stopTimeDao().getStopsForTrip(trip.trip_id)
                RouteDirection(
                    headsign = trip.trip_headsign ?: "Unknown Direction",
                    stops = stops
                )
            }
            _directions.value = dirs
        }
    }
}

class RouteDetailsViewModelFactory(private val application: Application, private val routeId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RouteDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RouteDetailsViewModel(application, routeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
