package com.katyrin.freemodule

import com.katyrin.freemodule.data.AppState
import com.katyrin.freemodule.data.Event
import com.katyrin.freemodule.storage.Storage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class CoinCounterViewModel(private val storage: Storage) {

    private val _sharedFlow =
        MutableSharedFlow<AppState>(REPLAY_NUMBER, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val sharedFlow: SharedFlow<AppState> = _sharedFlow.asSharedFlow()

    fun getCount() {
        _sharedFlow.tryEmit(AppState.ShowCount(storage.getCount().toString()))
    }

    fun setCount(count: Int) {
        storage.setCount(count)
        getCount()
    }

    fun checkCount(event: Event) {
        val totalCount = storage.getCount() + event.count
        if (totalCount >= 0) _sharedFlow.tryEmit(AppState.CallNumber(event))
        else _sharedFlow.tryEmit(AppState.ShowLackCount)
    }

    private companion object {
        const val REPLAY_NUMBER = 1
    }
}