package com.kurantsov.integritycheck.data.remote

import retrofit2.http.GET
import retrofit2.http.Header

internal interface SecretsApi {

    @GET("/secrets")
    fun getSecrets(
        @Header("X-Firebase-AppCheck") appCheckToken: String
    ): SecretsDto

}