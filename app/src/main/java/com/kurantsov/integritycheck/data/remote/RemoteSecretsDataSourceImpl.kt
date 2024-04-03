package com.kurantsov.integritycheck.data.remote

import com.google.firebase.appcheck.FirebaseAppCheck
import com.kurantsov.integritycheck.data.RemoteSecretsDataSource
import com.kurantsov.integritycheck.domain.Secrets
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class RemoteSecretsDataSourceImpl @Inject constructor(
    private val api: SecretsApi,
    private val appCheck: FirebaseAppCheck,
    private val mapper: SecretsDtoMapper,
) : RemoteSecretsDataSource {
    override suspend fun getSecrets(): Secrets {
        val token = appCheck.limitedUseAppCheckToken
            .await().token

        /*
        val dto = api.getSecrets(token)*/
        val client = HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }
        }
        val dto = client.get("http://192.168.0.177:8080/secrets") {
            headers {
                append("X-Firebase-AppCheck", token)
            }
        }.body<SecretsDto>()
        client.close()

        return mapper.map(dto)
    }
}