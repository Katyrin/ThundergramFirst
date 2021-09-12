package com.katyrin.freemodule.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast

private const val REQUEST_CODE = 500

fun Activity.toast(message: String?): Unit =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Activity.toast(resourceId: Int): Unit =
    Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show()

fun Activity.cellNumber(uri: Uri): Unit =
    startActivityForResult(Intent(Intent.ACTION_CALL, uri), REQUEST_CODE)