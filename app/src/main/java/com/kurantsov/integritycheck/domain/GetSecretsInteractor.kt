package com.kurantsov.integritycheck.domain

import javax.inject.Inject

class GetSecretsInteractor @Inject internal constructor(
    private val repository: SecretsRepository,
) {
    suspend operator fun invoke(): Secrets {
        return repository.getSecrets()
    }
}