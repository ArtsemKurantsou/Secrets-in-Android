package com.kurantsov.integritycheck.di

import com.google.firebase.appcheck.FirebaseAppCheck
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.kurantsov.integritycheck.data.RemoteSecretsDataSource
import com.kurantsov.integritycheck.data.SecretsRepositoryImpl
import com.kurantsov.integritycheck.data.remote.RemoteSecretsDataSourceImpl
import com.kurantsov.integritycheck.data.remote.SecretsApi
import com.kurantsov.integritycheck.domain.SecretsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SecretsModule {
    @Binds
    @Singleton
    fun bindRepository(impl: SecretsRepositoryImpl): SecretsRepository

    @Binds
    fun bindRemoteDataSource(impl: RemoteSecretsDataSourceImpl): RemoteSecretsDataSource

    companion object {
        @Provides
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("192.168.0.177:8081")
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .client(OkHttpClient())
                .build()
        }

        @Provides
        fun provideSecretsApi(retrofit: Retrofit): SecretsApi {
            return retrofit.create(SecretsApi::class.java)
        }

        @Provides
        fun provideAppCheck(): FirebaseAppCheck {
            return FirebaseAppCheck.getInstance()
        }
    }
}