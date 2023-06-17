package com.kurantsov.integritycheck.domain

interface SecretsRepository {
    suspend fun getSecrets(): Secrets
}