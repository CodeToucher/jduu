package com.judu.transport.ui.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.judu.transport.data.db.entities.Route
import com.judu.transport.ui.viewmodel.TimetableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel = viewModel(),
    onRouteClick: (String) -> Unit
) {
    val routes by viewModel.routes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showSettings by remember { mutableStateOf(false) }
    val isWifiOnly by viewModel.isWifiOnly.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val lastUpdate by viewModel.lastUpdateTime.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetables") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search routes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            if (routes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (searchQuery.isNotEmpty()) {
                         Text("No routes found")
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isUpdating) {
                                CircularProgressIndicator()
                                Text("Updating database...", modifier = Modifier.padding(top = 16.dp))
                            } else {
                                CircularProgressIndicator()
                                Text("Loading routes...", modifier = Modifier.padding(top = 16.dp))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Group by transport type
                    val grouped = routes.groupBy { it.route_type }
                    
                    grouped.forEach { (type, typeRoutes) ->
                        item {
                            Text(
                                text = getTransportTypeName(type),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(typeRoutes.sortedBy { it.route_short_name }) { route ->
                            RouteItem(route = route, onClick = { onRouteClick(route.route_id) })
                            Divider()
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSettings = false },
            title = { Text("Data Settings") },
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleWifiOnly(!isWifiOnly) }
                    ) {
                        androidx.compose.material3.Checkbox(checked = isWifiOnly, onCheckedChange = { viewModel.toggleWifiOnly(it) })
                        Text("Update on Wi-Fi only")
                    }
                    
                    androidx.compose.material3.Button(
                        onClick = { viewModel.updateData(force = true); showSettings = false },
                        enabled = !isUpdating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(if (isUpdating) "Updating..." else "Update Database Now")
                    }
                    
                    if (lastUpdate > 0) {
                        Text(
                            "Last updated: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(lastUpdate))}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showSettings = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun RouteItem(route: Route, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(route.route_short_name) },
        supportingContent = { Text(route.route_long_name) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

fun getTransportTypeName(type: Int): String {
    return when (type) {
        0 -> "Trolleybuses" // Trams in GTFS standard, but usually used for Trolleybuses in Vilnius if defined as 0 or 900
        3 -> "Buses"
        800 -> "Trolleybuses" // Sometimes
        else -> "Other ($type)"
    }
}
