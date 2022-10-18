package com.rtchagas.pingplacepicker.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

typealias ActivityInflater<T> = (LayoutInflater) -> T

abstract class BaseActivity<T : ViewBinding>(
    private val inflate: ActivityInflater<T>
) : AppCompatActivity() {

    private var _binding: T? = null

    /**
     * The view binding for this fragment.
     *
     * [https://developer.android.com/topic/libraries/view-binding]
     *
     * This property is only valid between [onCreate] and [onDestroy].
     */
    val binding: T get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
