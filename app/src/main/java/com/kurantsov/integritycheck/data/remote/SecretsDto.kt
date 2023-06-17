package com.kurantsov.integritycheck.data.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class SecretsDto(
    val apiKey: String,
    val apiPassword: String,
)