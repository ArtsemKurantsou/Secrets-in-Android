package com.kurantsov.integritycheck.data.remote

import com.kurantsov.integritycheck.domain.Secrets
import javax.inject.Inject

internal class SecretsDtoMapper @Inject constructor() {
    fun map(dto: SecretsDto): Secrets {
        return Secrets(
            serverApiKey = dto.apiKey,
            serverApiPassword = dto.apiPassword
        )
    }
}
