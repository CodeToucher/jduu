package com.judu.transport.ui.navigation

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Timetable : Screen("timetable")
    object RouteDetails : Screen("route_details/{routeId}") {
        fun createRoute(routeId: String) = "route_details/$routeId"
    }
    object StopSchedule : Screen("stop_schedule/{routeId}/{stopId}/{stopName}") {
        fun createRoute(routeId: String, stopId: String, stopName: String) = "stop_schedule/$routeId/$stopId/$stopName"
    }
}
