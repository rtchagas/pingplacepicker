package com.rtchagas.pingplacepicker.ui

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

internal fun View.onclick(callback: () -> Unit): Disposable = clicks()
    .throttleFirst(1, TimeUnit.SECONDS)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe {
        callback()
    }

/**
 * Display the simple Toast message with the [Toast.LENGTH_SHORT] duration.
 *
 * @param message the message text.
 */
fun Context.toast(message: CharSequence): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply { show() }

/**
 * Display the simple Toast message with the [Toast.LENGTH_SHORT] duration.
 *
 * @param resId the resource ID of the message text.
 */
fun Context.toast(@StringRes resId: Int): Toast = Toast
    .makeText(this, resId, Toast.LENGTH_SHORT)
    .apply { show() }
