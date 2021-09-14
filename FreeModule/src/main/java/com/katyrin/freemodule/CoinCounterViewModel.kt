package com.katyrin.freemodule

import androidx.lifecycle.ViewModel
import com.katyrin.freemodule.bus.EventBus
import com.katyrin.freemodule.data.AppState
import com.katyrin.freemodule.data.Event
import com.katyrin.freemodule.storage.Storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest

class CoinCounterViewModel(private val storage: Storage): ViewModel() {

    private val coroutineScope = CoroutineScope(
        Dispatchers.Main
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    )

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

    private fun checkCount(event: Event) {
        val totalCount = storage.getCount() + event.count
        if (totalCount >= 0) _sharedFlow.tryEmit(AppState.CallNumber(event))
        else _sharedFlow.tryEmit(AppState.ShowLackCount)
    }

    private fun handleError(error: Throwable) {
        _sharedFlow.tryEmit(AppState.Error(error.message))
    }

    private fun cancelJob(): Unit = coroutineScope.coroutineContext.cancelChildren()

    override fun onCleared() {
        cancelJob()
        super.onCleared()
    }

    init {
        coroutineScope.launch { EventBus.events.collectLatest(::checkCount) }
    }

    private companion object {
        const val REPLAY_NUMBER = 1
    }
}