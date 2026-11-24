package com.example.appsupabaseis2026_2.models

import kotlinx.serialization.Serializable
@Serializable
data class Materia(
    val id: Long,
    val nombre: String? = null,
    val nivel: Int? = null
)

