package com.katyrin.freemodule.utils

import android.widget.Toast
import android.app.Fragment

fun Fragment.toast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}