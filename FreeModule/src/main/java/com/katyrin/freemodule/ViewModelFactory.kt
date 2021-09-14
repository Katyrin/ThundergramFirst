package com.katyrin.freemodule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.katyrin.freemodule.storage.Storage

class ViewModelFactory(private val storage: Storage) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoinCounterViewModel::class.java))
            return CoinCounterViewModel(storage) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}