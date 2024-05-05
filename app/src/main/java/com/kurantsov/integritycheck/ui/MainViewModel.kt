package com.kurantsov.integritycheck.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurantsov.integritycheck.domain.GetSecretsInteractor
import com.kurantsov.integritycheck.domain.Secrets
import com.kurantsov.integritycheck.domain.SecretsSourceType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class MainViewModel @Inject constructor(
    private val getSecretsInteractor: GetSecretsInteractor,
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.NotLoaded)
    val state: StateFlow<State> = _state

    fun onLoad(type: SecretsSourceType) {
        viewModelScope.launch {
            runCatching {
                _state.value = State.Loading
                getSecretsInteractor(type)
            }.onSuccess { secrets ->
                _state.value = State.Success(secrets)
            }.onFailure { e ->
                if (e !is CancellationException) {
                    Log.e("MainViewModel", "Error loading secrets", e)
                    _state.value = State.Error(e.message ?: "Secrets fetch failed")
                }
            }
        }
    }

    fun onBack() {
        _state.value = State.NotLoaded
    }

    sealed class State {
        data object NotLoaded : State()
        data object Loading : State()
        data class Success(val secrets: Secrets) : State()
        data class Error(val message: String) : State()
    }
}