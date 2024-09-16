package com.sumeyra.morethanastory.viewmodel.states

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}