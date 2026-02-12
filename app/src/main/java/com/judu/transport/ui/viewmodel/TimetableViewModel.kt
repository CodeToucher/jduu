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
    
    // Preferences
    private val prefs = application.getSharedPreferences("judu_prefs", android.content.Context.MODE_PRIVATE)
    private val _isWifiOnly = MutableStateFlow(prefs.getBoolean("wifi_only", true))
    val isWifiOnly: StateFlow<Boolean> = _isWifiOnly

    private val _lastUpdateTime = MutableStateFlow(prefs.getLong("last_update_time", 0))
    val lastUpdateTime: StateFlow<Long> = _lastUpdateTime

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    fun toggleWifiOnly(enabled: Boolean) {
        _isWifiOnly.value = enabled
        prefs.edit().putBoolean("wifi_only", enabled).apply()
    }

    fun updateData(force: Boolean = false) {
        viewModelScope.launch {
            if (_isUpdating.value) return@launch
            
            // Check Wifi if needed
            if (!force && _isWifiOnly.value) {
                // Todo: check network connectivity manager. For now assume yes or let user force it.
            }

            _isUpdating.value = true
            try {
                gtfsRepository.updateGtfsData()
                val now = System.currentTimeMillis()
                _lastUpdateTime.value = now
                prefs.edit().putLong("last_update_time", now).apply()
            } finally {
                _isUpdating.value = false
            }
        }
    }
    
    fun refreshData() {
       updateData(force = true)
    }
}
