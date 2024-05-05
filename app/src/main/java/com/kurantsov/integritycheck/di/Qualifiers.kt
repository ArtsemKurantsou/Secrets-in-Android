package com.kurantsov.integritycheck.di

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(
    allowedTargets = [
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER,
    ]
)
annotation class RemoteConfigDataSource


@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(
    allowedTargets = [
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER,
    ]
)
annotation class BackendDataSource
