package com.kurantsov.integritycheck.data.config

import com.kurantsov.integritycheck.domain.Secrets
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class RemoteConfigDtoMapper(private val json: Json) {
    @Inject
    constructor() : this(Json { ignoreUnknownKeys = true })

    operator fun invoke(dtoString: String): Secrets {
        val dto = json.decodeFromString<RemoteConfigDTO>(dtoString)
        return Secrets(
            serverApiKey = dto.apiKey,
            serverApiPassword = dto.apiPassword,
        )
    }

    @Serializable
    private data class RemoteConfigDTO(
        @SerialName("API_KEY")
        val apiKey: String,
        @SerialName("API_PASSWORD")
        val apiPassword: String,
    )
}