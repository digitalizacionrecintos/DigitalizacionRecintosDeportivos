package org.example.project.domain.model

data class ManagerEvent(
        val id: String,
        val title: String,
        val date: String,
        val location: String,
        val categoryName: String,
        val imagenUrl: String? = null
)
