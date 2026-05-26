package com.rtchagas.pingplacepicker.ui

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Debounced click listener — ignores subsequent clicks within [intervalMs] of the
 * last accepted click. Replaces RxBinding's `throttleFirst`.
 */
internal fun View.onClickDebounced(intervalMs: Long = 1000L, callback: () -> Unit) {
    var lastClickAt = 0L
    setOnClickListener {
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickAt >= intervalMs) {
            lastClickAt = now
            callback()
        }
    }
}

fun Context.toast(message: CharSequence): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply { show() }

fun Context.toast(@StringRes resId: Int): Toast = Toast
    .makeText(this, resId, Toast.LENGTH_SHORT)
    .apply { show() }
