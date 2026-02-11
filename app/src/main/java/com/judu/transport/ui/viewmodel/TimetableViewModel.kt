package com.judu.transport.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.judu.transport.data.db.AppDatabase
import com.judu.transport.data.db.entities.Route
import com.judu.transport.data.repository.GtfsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TimetableViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val gtfsRepository = GtfsRepository(application, database)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val routes: StateFlow<List<Route>> = combine(
        database.routeDao().getAllRoutes(),
        _searchQuery
    ) { routes, query ->
        if (query.isBlank()) {
            routes
        } else {
            routes.filter { 
                it.route_short_name.contains(query, ignoreCase = true) || 
                it.route_long_name.contains(query, ignoreCase = true) 
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    init {
        // Trigger GTFS update if needed, or maybe just once.
        // For now, let's trigger it here to ensure data exists.
        // In a real app, use WorkManager.
        viewModelScope.launch {
            if (database.routeDao().getAllRoutes().stateIn(viewModelScope).value.isEmpty()) {
                 gtfsRepository.updateGtfsData()
            }
        }
    }
    
    fun refreshData() {
        viewModelScope.launch {
            gtfsRepository.updateGtfsData()
        }
    }
}
