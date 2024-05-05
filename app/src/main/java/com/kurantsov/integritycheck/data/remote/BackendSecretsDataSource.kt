package com.kurantsov.integritycheck.data.remote

import com.google.firebase.appcheck.FirebaseAppCheck
import com.kurantsov.integritycheck.data.RemoteSecretsDataSource
import com.kurantsov.integritycheck.domain.Secrets
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class BackendSecretsDataSource @Inject constructor(
    private val client: HttpClient,
    private val appCheck: FirebaseAppCheck,
    private val mapper: SecretsDtoMapper,
) : RemoteSecretsDataSource {
    override suspend fun getSecrets(): Secrets = withContext(Dispatchers.IO) {
        // Fetches AppCheck token
        val token = appCheck.limitedUseAppCheckToken
            .await()
            .token

        // Sends request to the Backend with a token to get secrets
        val dto = client.get("/secrets") {
            headers {
                append("X-Firebase-AppCheck", token)
            }
        }.body<SecretsDto>()

        return@withContext mapper.map(dto)
    }
}