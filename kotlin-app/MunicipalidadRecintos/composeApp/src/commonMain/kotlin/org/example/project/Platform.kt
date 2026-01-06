package org.example.project

expect fun getPlatform(): Platform

interface Platform {
    val name: String
    fun openMaps(lat: Double, lng: Double, label: String = "")
}
