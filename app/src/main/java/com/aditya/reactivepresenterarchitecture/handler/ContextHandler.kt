package com.aditya.reactivepresenterarchitecture.handler

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import com.google.android.material.internal.ContextUtils

fun Context.print(message: String) {
    if (!isValid(this)) return
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun isValid(context: Context?): Boolean {
    if (context == null) return false

    if (context is Activity) {
        return !context.isDestroyed
    } else if (context is ContextWrapper) {
        @SuppressLint("RestrictedApi")
        val activity = ContextUtils.getActivity(context.baseContext)
        if (activity != null) return !activity.isDestroyed
    }
    return false
}
