package org.example.project

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
            UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override fun openMaps(lat: Double, lng: Double, label: String) {

        val urlString = "http://maps.apple.com/?ll=$lat,$lng&q=${label.replace(" ", "+")}"
        val url = NSURL.URLWithString(urlString)

        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
