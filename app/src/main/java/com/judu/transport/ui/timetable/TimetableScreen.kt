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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// ... (in the TimetableScreen function) ...

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
