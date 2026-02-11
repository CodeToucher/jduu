package com.judu.transport.ui.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.judu.transport.data.db.entities.Stop
import com.judu.transport.ui.viewmodel.RouteDetailsViewModel
import com.judu.transport.ui.viewmodel.RouteDetailsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(
    routeId: String,
    onBackClick: () -> Unit = {},
    onStopClick: (String, String) -> Unit = { _, _ -> }
) {
    val application = LocalContext.current.applicationContext as android.app.Application
    val viewModel: RouteDetailsViewModel = viewModel(
        factory = RouteDetailsViewModelFactory(application, routeId)
    )
    
    val route by viewModel.route.collectAsState()
    val directions by viewModel.directions.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(route?.route_short_name ?: "")
                        Text(route?.route_long_name ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (directions.isNotEmpty()) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    directions.forEachIndexed { index, direction ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(direction.headsign) }
                        )
                    }
                }
                
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(directions[selectedTabIndex].stops) { stop ->
                        StopItem(stop, onClick = { onStopClick(stop.stop_id, stop.stop_name) })
                        Divider()
                    }
                }
            } else {
                 Text("Loading stops...", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun StopItem(stop: Stop, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(vertical = 12.dp, horizontal = 16.dp)) {
        Text(stop.stop_name, style = MaterialTheme.typography.bodyLarge)
    }
}
