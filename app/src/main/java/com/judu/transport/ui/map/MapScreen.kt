package com.judu.transport.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.judu.transport.R
import com.judu.transport.ui.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val vehicles by viewModel.vehicles.collectAsState()

    // Initialize OSM Configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        // Set user agent to avoid getting banned
        Configuration.getInstance().userAgentValue = "JuduTransportApp/1.0"
    }
    
    // Permission Handling
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(54.6872, 25.2797)) // Vilnius

                    // Location Overlay
                    if (hasLocationPermission) {
                        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                        locationOverlay.enableMyLocation()
                        overlays.add(locationOverlay)
                    }
                }
            },
            update = { mapView ->
                // Efficiently update markers
                // In a real app, use a FolderOverlay or specific logic to reusing markers to avoid flicker
                // For now, clear vehicle markers and re-add.
                // NOTE: We should KEEP the LocationOverlay.
                
                // Remove all markers but keep LocationOverlay (usually index 0 or first one)
                // Safer: remove all of type Marker
                mapView.overlays.removeAll { it is Marker }
                
                vehicles.forEach { vehicle ->
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(vehicle.lat, vehicle.lon)
                    marker.rotation = -vehicle.bearing.toFloat() // OSM rotation might be counter-clockwise
                    marker.title = "${vehicle.transportType} ${vehicle.routeNumber}"
                    marker.snippet = "Vehicle: ${vehicle.vehicleId}"
                    
                    val color = if (vehicle.transportType == "Troleibusai") Color.Red else Color.Blue
                    val iconKey = "${vehicle.transportType}_${color.toArgb()}"
                    
                    // Convert drawable to bitmap for OSM
                    // We can reuse the BitmapHelper logic but need to return Drawable
                    marker.icon = getColoredDrawable(context, R.drawable.ic_navigation, color.toArgb())
                    
                    // Anchor center
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    
                    mapView.overlays.add(marker)
                }
                
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Lifecycle observer for MapView
        DisposableEffect(lifecycleOwner) {
            val observer = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    // mapView.onResume() - can't easily access mapView instance here unless we remember it.
                    // osmdroid 6+ handles some lifecycle automatically if View is attached/detached? 
                    // Actually recommended to call onResume/onPause on the MapView.
                }
                override fun onPause(owner: LifecycleOwner) {
                    // mapView.onPause()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}

fun getColoredDrawable(context: Context, resId: Int, color: Int): Drawable {
    val drawable = ContextCompat.getDrawable(context, resId)!!.mutate()
    drawable.setTint(color)
    return drawable
}
