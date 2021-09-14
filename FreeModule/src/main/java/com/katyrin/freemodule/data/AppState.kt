package com.katyrin.freemodule.data

sealed class AppState {
    data class ShowCount(val count: String) : AppState()
    data class CallNumber(val event: Event) : AppState()
    data class Error(val message: String?): AppState()
    object ShowLackCount : AppState()
}
