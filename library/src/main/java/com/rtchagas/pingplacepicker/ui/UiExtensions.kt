package com.rtchagas.pingplacepicker.ui

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

fun <T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    minState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (value: T) -> Unit,
): Job =
    this
        .onEach(action)
        .flowWithLifecycle(lifecycleOwner.lifecycle, minState)
        .launchIn(lifecycleOwner.lifecycleScope)

