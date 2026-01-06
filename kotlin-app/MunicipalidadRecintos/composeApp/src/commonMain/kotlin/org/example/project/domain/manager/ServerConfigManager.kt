package org.example.project.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ServerConfigManager {
    private val _serverUrl = MutableStateFlow<String?>(null)
    val serverUrl: StateFlow<String?> = _serverUrl.asStateFlow()

    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    fun setServerConfig(ip: String, port: String) {
        val url = "http://$ip:$port/api/"
        _serverUrl.value = url
        _isConfigured.value = true
    }

    fun getServerUrl(): String {
        return _serverUrl.value ?: "http://192.168.1.144:8080/api/"
    }

    fun resetConfig() {
        _serverUrl.value = null
        _isConfigured.value = false
    }
}
