package com.katyrin.freemodule.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

private const val REQUEST_CODE = 500

fun Fragment.toast(message: String?): Unit =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

fun AppCompatActivity.toast(message: String?): Unit =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Fragment.toast(resourceId: Int): Unit =
    Toast.makeText(requireContext(), resourceId, Toast.LENGTH_SHORT).show()

fun Activity.cellNumber(uri: Uri): Unit =
    startActivityForResult(Intent(Intent.ACTION_CALL, uri), REQUEST_CODE)

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted { this@launchWhenStarted.collect() }
}