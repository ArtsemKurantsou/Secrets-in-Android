package com.kurantsov.integritycheck.data

import com.kurantsov.integritycheck.domain.Secrets

internal interface RemoteSecretsDataSource {
    suspend fun getSecrets(): Secrets
}