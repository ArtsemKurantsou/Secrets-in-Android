package com.kurantsov.integritycheck.data.remote

import com.google.firebase.appcheck.FirebaseAppCheck
import com.kurantsov.integritycheck.data.RemoteSecretsDataSource
import com.kurantsov.integritycheck.domain.Secrets
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class RemoteSecretsDataSourceImpl @Inject constructor(
    private val api: SecretsApi,
    private val appCheck: FirebaseAppCheck,
    private val mapper: SecretsDtoMapper,
) : RemoteSecretsDataSource {
    override suspend fun getSecrets(): Secrets {
        val token = appCheck.limitedUseAppCheckToken
            .await()
        val dto = api.getSecrets(token.token)
        return mapper.map(dto)
    }
}