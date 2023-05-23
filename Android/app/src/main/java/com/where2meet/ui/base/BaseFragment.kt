package com.where2meet.ui.base

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import logcat.logcat
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    @Inject protected lateinit var imageLoader: ImageLoader
    protected lateinit var eventJob: Job

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    abstract fun bindView()

    override fun onStop() {
        super.onStop()
        if (this::eventJob.isInitialized) {
            eventJob.cancel()
        }
    }

    protected fun hideKeyboard() {
        try {
            val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        } catch (e: Exception) {
            logcat { e.message.toString() }
        }
    }

    protected fun navigateTo(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}
