package com.kurantsov.integritycheck.data

import com.kurantsov.integritycheck.di.BackendDataSource
import com.kurantsov.integritycheck.di.RemoteConfigDataSource
import com.kurantsov.integritycheck.domain.Secrets
import com.kurantsov.integritycheck.domain.SecretsRepository
import com.kurantsov.integritycheck.domain.SecretsSourceType
import javax.inject.Inject

internal class SecretsRepositoryImpl @Inject constructor(
    @param:BackendDataSource private val backendDataSource: RemoteSecretsDataSource,
    @param:RemoteConfigDataSource private val configDataSource: RemoteSecretsDataSource,
) : SecretsRepository {
    override suspend fun getSecrets(sourceType: SecretsSourceType): Secrets {
        val source = when (sourceType) {
            SecretsSourceType.BACKEND -> backendDataSource
            SecretsSourceType.REMOTE_CONFIG -> configDataSource
        }
        return source.getSecrets()
    }
}