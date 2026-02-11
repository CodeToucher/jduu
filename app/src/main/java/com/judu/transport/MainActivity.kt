package com.judu.transport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.judu.transport.ui.map.MapScreen
import com.judu.transport.ui.navigation.Screen
import com.judu.transport.ui.theme.JuduTransportTheme
import com.judu.transport.ui.timetable.TimetableScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JuduTransportTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Place, contentDescription = "Map") },
                                label = { Text("Map") },
                                selected = currentDestination?.route == Screen.Map.route,
                                onClick = {
                                    navController.navigate(Screen.Map.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.DateRange, contentDescription = "Timetable") },
                                label = { Text("Timetable") },
                                selected = currentDestination?.route == Screen.Timetable.route,
                                onClick = {
                                    navController.navigate(Screen.Timetable.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Map.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Map.route) {
                            MapScreen()
                        }
                        composable(Screen.Timetable.route) {
                            TimetableScreen(
                                onRouteClick = { routeId ->
                                    navController.navigate(Screen.RouteDetails.createRoute(routeId))
                                }
                            )
                        }
                        composable(Screen.RouteDetails.route) { backStackEntry ->
                            val routeId = backStackEntry.arguments?.getString("routeId")
                            if (routeId != null) {
                                com.judu.transport.ui.timetable.RouteDetailsScreen(
                                    routeId = routeId,
                                    onBackClick = { navController.popBackStack() },
                                    onStopClick = { stopId, stopName ->
                                        navController.navigate(Screen.StopSchedule.createRoute(routeId, stopId, stopName))
                                    }
                                )
                            }
                        }
                        composable(Screen.StopSchedule.route) { backStackEntry ->
                            val routeId = backStackEntry.arguments?.getString("routeId")
                            val stopId = backStackEntry.arguments?.getString("stopId")
                            val stopName = backStackEntry.arguments?.getString("stopName")
                            if (routeId != null && stopId != null && stopName != null) {
                                com.judu.transport.ui.timetable.StopScheduleScreen(
                                    routeId = routeId, 
                                    stopId = stopId, 
                                    stopName = stopName,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
