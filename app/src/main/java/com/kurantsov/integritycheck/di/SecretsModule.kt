package com.kurantsov.integritycheck.di

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kurantsov.integritycheck.BuildConfig
import com.kurantsov.integritycheck.data.RemoteSecretsDataSource
import com.kurantsov.integritycheck.data.SecretsRepositoryImpl
import com.kurantsov.integritycheck.data.config.RemoteConfigSecretsDataSource
import com.kurantsov.integritycheck.data.remote.BackendSecretsDataSource
import com.kurantsov.integritycheck.domain.SecretsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SecretsModule {
    @Binds
    @Singleton
    fun bindRepository(impl: SecretsRepositoryImpl): SecretsRepository

    @Binds
    @BackendDataSource
    fun bindBackendDataSource(impl: BackendSecretsDataSource): RemoteSecretsDataSource

    @Binds
    @RemoteConfigDataSource
    fun bindRemoteConfigDataSource(impl: RemoteConfigSecretsDataSource): RemoteSecretsDataSource

    companion object {
        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient {
            return HttpClient(Android) {
                defaultRequest {
                    url("http://${BuildConfig.BUILD_MACHINE_IP}:8080")
                }
                expectSuccess = true
                install(ContentNegotiation) {
                    json()
                }
            }
        }

        @Provides
        fun provideAppCheck(): FirebaseAppCheck {
            return FirebaseAppCheck.getInstance()
        }

        @Provides
        fun provideRemoteConfig(): FirebaseRemoteConfig {
            return FirebaseRemoteConfig.getInstance()
        }
    }
}