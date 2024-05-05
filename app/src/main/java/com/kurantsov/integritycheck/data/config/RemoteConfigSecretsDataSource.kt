package com.kurantsov.integritycheck.data.config

import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kurantsov.integritycheck.data.RemoteSecretsDataSource
import com.kurantsov.integritycheck.domain.Secrets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

internal class RemoteConfigSecretsDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val remoteConfigDtoMapper: RemoteConfigDtoMapper,
) : RemoteSecretsDataSource {
    private val configurationTask: Task<Void>

    init {
        val configuration = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(FETCH_INTERVAL.inWholeSeconds)
            .build()
        configurationTask = remoteConfig.setConfigSettingsAsync(configuration)
    }

    override suspend fun getSecrets(): Secrets = withContext(Dispatchers.IO) {
        ensureConfigFetched()
        val dtoString = remoteConfig.getString(SECRETS_KEY)
        return@withContext remoteConfigDtoMapper(dtoString)
    }

    private suspend fun ensureConfigFetched() {
        configurationTask.await()
        if (remoteConfig.info.lastFetchStatus == FirebaseRemoteConfig.LAST_FETCH_STATUS_NO_FETCH_YET
            || (System.currentTimeMillis() - remoteConfig.info.fetchTimeMillis).milliseconds >= FETCH_INTERVAL
        ) {
            remoteConfig.fetchAndActivate().await()
        }
    }

    private companion object {
        const val SECRETS_KEY = "SECRETS"
        val FETCH_INTERVAL: Duration = 1.days
    }
}
