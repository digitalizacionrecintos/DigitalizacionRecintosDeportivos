package org.example.project

import android.content.Intent
import android.net.Uri
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun openMaps(lat: Double, lng: Double, label: String) {
        val context = ContextHolder.getContext()
        if (context != null) {
            try {

                val uriString = "geo:$lat,$lng?q=$lat,$lng($label)"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e: Exception) {}
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
