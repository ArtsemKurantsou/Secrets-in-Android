package com.kurantsov.integritycheck.data

import com.kurantsov.integritycheck.domain.Secrets
import com.kurantsov.integritycheck.domain.SecretsRepository
import javax.inject.Inject

internal class SecretsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteSecretsDataSource,
) : SecretsRepository {
    override suspend fun getSecrets(): Secrets {
        return remoteDataSource.getSecrets()
    }
}