package com.katyrin.freemodule

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.katyrin.freemodule.databinding.FragmentCoinCounterBinding
import com.katyrin.freemodule.storage.StorageImpl
import com.katyrin.freemodule.data.Event
import com.katyrin.freemodule.bus.EventBus
import com.katyrin.freemodule.data.AppState
import com.katyrin.freemodule.utils.cellNumber
import com.katyrin.freemodule.utils.toast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CoinCounterFragment : Fragment() {

    private val currentActivity: Activity by lazy { activity }
    private var binding: FragmentCoinCounterBinding? = null
    private val viewModel: CoinCounterViewModel by lazy {
        CoinCounterViewModel(StorageImpl(currentActivity))
    }

    private val coroutineScope = CoroutineScope(
        Dispatchers.Main
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable -> handleError(throwable) }
    )

    private fun handleError(error: Throwable): Unit = currentActivity.toast(error.message)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCoinCounterBinding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState ?: viewModel.getCount()
        initViews()
        getCoroutinesResult()
    }

    private fun initViews() {
        binding?.apply {
            payButton.setOnClickListener { viewModel.setCount(10) }
            adsButton.setOnClickListener { viewModel.setCount(1) }
        }
    }

    private fun getCoroutinesResult() {
        coroutineScope.launch { EventBus.events.collectLatest(::renderEvent) }
        viewModel.sharedFlow
            .onEach(::renderData)
            .launchIn(coroutineScope)
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.ShowCount -> binding?.countTextView?.text = appState.count
            is AppState.CallNumber -> callNumberState(appState.event)
            is AppState.ShowLackCount -> currentActivity.toast(R.string.lack_count_message)
        }
    }

    private fun callNumberState(event: Event) {
        currentActivity.cellNumber(event.uri)
        viewModel.setCount(event.count)
    }

    private fun renderEvent(event: Event): Unit = viewModel.checkCount(event)

    private fun cancelJob(): Unit = coroutineScope.coroutineContext.cancelChildren()

    override fun onDestroyView() {
        binding = null
        cancelJob()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): Fragment = CoinCounterFragment()
    }
}