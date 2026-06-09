package org.example.project.domain.manager

object PreferencesManager {
    private const val KEY_HAS_SEEN_INSCRIPTION_TUTORIAL = "has_seen_inscription_tutorial"

    fun hasSeenInscriptionTutorial(): Boolean {
        return getBoolean(KEY_HAS_SEEN_INSCRIPTION_TUTORIAL, false)
    }

    fun setHasSeenInscriptionTutorial(seen: Boolean) {
        setBoolean(KEY_HAS_SEEN_INSCRIPTION_TUTORIAL, seen)
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return PreferencesStorage.getBoolean(key, defaultValue)
    }

    private fun setBoolean(key: String, value: Boolean) {
        PreferencesStorage.setBoolean(key, value)
    }
}

expect object PreferencesStorage {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun setBoolean(key: String, value: Boolean)
    fun getString(key: String, defaultValue: String?): String?
    fun setString(key: String, value: String?)
}

object PreferenceKeys {
    const val SERVER_IP = "server_ip"
    const val SERVER_PORT = "server_port"
}
