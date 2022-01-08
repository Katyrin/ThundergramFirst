package com.katyrin.freemodule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.katyrin.freemodule.storage.Storage

class ViewModelFactory(private val storage: Storage) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(CoinCounterViewModel::class.java))
            CoinCounterViewModel(storage) as T
        else throw IllegalArgumentException("Unknown ViewModel class")
}