package com.sumeyra.morethanastory.viewmodel.states


sealed class PostState {
    object Idle : PostState()
    object Loading : PostState()
    object Success : PostState()
    data class Error(val errorMessage: String) : PostState()
}