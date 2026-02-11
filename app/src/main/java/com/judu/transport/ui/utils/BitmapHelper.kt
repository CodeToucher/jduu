package com.judu.transport.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapHelper {
    fun vectorToBitmap(context: Context, @DrawableRes id: Int, color: Int? = null): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, id) ?: return BitmapDescriptorFactory.defaultMarker()
        
        if (color != null) {
            vectorDrawable.setTint(color)
        }
        
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
