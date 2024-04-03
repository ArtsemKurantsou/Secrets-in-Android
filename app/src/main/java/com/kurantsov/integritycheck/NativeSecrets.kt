package com.kurantsov.integritycheck

object NativeSecrets {
    init {
        System.loadLibrary("secrets")
    }

    external fun getApiKeyFromNative(): String

}
