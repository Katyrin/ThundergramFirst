package com.katyrin.freemodule.storage

import android.content.Context
import android.content.SharedPreferences
import com.katyrin.freemodule.R

class StorageImpl(context: Context): Storage {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.prefs_key), Context.MODE_PRIVATE)

    override fun setCount(count: Int) {
        val newCount = getCount() + count
        val editor = prefs.edit()
        editor.putInt(SHARED_PREFERENCES, newCount)
        editor.apply()
    }

    override fun getCount(): Int = prefs.getInt(SHARED_PREFERENCES, 10)

    private companion object {
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
    }
}