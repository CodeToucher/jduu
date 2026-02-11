package com.judu.transport.ui.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.judu.transport.data.db.entities.StopTime
import com.judu.transport.ui.viewmodel.StopScheduleViewModel
import com.judu.transport.ui.viewmodel.StopScheduleViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopScheduleScreen(
    routeId: String,
    stopId: String,
    stopName: String,
    onBackClick: () -> Unit
) {
    val application = LocalContext.current.applicationContext as android.app.Application
    val viewModel: StopScheduleViewModel = viewModel(
        factory = StopScheduleViewModelFactory(application, routeId, stopId, stopName)
    )
    val schedule by viewModel.schedule.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                     Column {
                        Text(stopName)
                        Text("Schedule", style = MaterialTheme.typography.bodySmall)
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(schedule) { stopTime ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(
                        text = stopTime.arrival_time?.substring(0, 5) ?: "--:--",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    // We could show trip details here if we joined with Trips
                }
                Divider()
            }
        }
    }
}
