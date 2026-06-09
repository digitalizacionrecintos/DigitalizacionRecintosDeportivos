package org.example.project.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ServerConfigManager {
    private val _ip = MutableStateFlow("")
    val ip: StateFlow<String> = _ip.asStateFlow()

    private val _port = MutableStateFlow("")
    val port: StateFlow<String> = _port.asStateFlow()

    private val _serverUrl = MutableStateFlow<String?>(null)
    val serverUrl: StateFlow<String?> = _serverUrl.asStateFlow()

    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured.asStateFlow()

    init {
        loadSavedConfig()
    }

    private fun loadSavedConfig() {
        val savedIp = PreferencesStorage.getString(PreferenceKeys.SERVER_IP, null)
        val savedPort = PreferencesStorage.getString(PreferenceKeys.SERVER_PORT, null)
        if (savedIp != null && savedPort != null) {
            _ip.value = savedIp
            _port.value = savedPort
            val url = "http://$savedIp:$savedPort/api/"
            _serverUrl.value = url
            _isConfigured.value = true
        }
    }

    fun setServerConfig(ip: String, port: String) {
        _ip.value = ip
        _port.value = port
        val url = "http://$ip:$port/api/"
        _serverUrl.value = url
        _isConfigured.value = true
        PreferencesStorage.setString(PreferenceKeys.SERVER_IP, ip)
        PreferencesStorage.setString(PreferenceKeys.SERVER_PORT, port)
    }

    fun getServerUrl(): String {
        return _serverUrl.value ?: "http://192.168.1.144:8080/api/"
    }

    fun resetConfig() {
        _ip.value = ""
        _port.value = ""
        _serverUrl.value = null
        _isConfigured.value = false
        PreferencesStorage.setString(PreferenceKeys.SERVER_IP, null)
        PreferencesStorage.setString(PreferenceKeys.SERVER_PORT, null)
    }
}
