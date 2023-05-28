package com.where2meet.ui.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.where2meet.ui.ext.GetBindMethod
import com.where2meet.ui.ext.MainHandler
import dagger.hilt.android.internal.ThreadUtil.ensureMainThread
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// from https://medium.com/@hoc081098/viewbinding-delegate-one-line-4d0cdcbf53ba
class FragmentViewBindingDelegate<T : ViewBinding> private constructor(
    private val fragment: Fragment,
    viewBindingBind: ((View) -> T)? = null,
    viewBindingClazz: Class<T>? = null,
) : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null
    private val bind = viewBindingBind ?: { view: View ->
        @Suppress("UNCHECKED_CAST")
        GetBindMethod(viewBindingClazz!!)(null, view) as T
    }

    init {
        ensureMainThread()
        require(viewBindingBind != null || viewBindingClazz != null) {
            "Both viewBindingBind and viewBindingClazz are null. Please provide at least one."
        }

        fragment.lifecycle.addObserver(FragmentLifecycleObserver())
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding?.let { return it }

        check(
            fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(
                Lifecycle.State.INITIALIZED,
            ),
        ) {
            "Attempt to get view binding when fragment view is destroyed"
        }

        return bind(thisRef.requireView()).also { binding = it }
    }

    private inner class FragmentLifecycleObserver : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            fragment.viewLifecycleOwnerLiveData
                .observe(fragment) { viewLifecycleOwner: LifecycleOwner? ->
                    viewLifecycleOwner ?: return@observe

                    val viewLifecycleObserver = object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            viewLifecycleOwner.lifecycle.removeObserver(this)
                            MainHandler.post {
                                binding = null
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycle.addObserver(viewLifecycleObserver)
                }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            fragment.lifecycle.removeObserver(this)
            binding = null
        }
    }

    companion object Factory {
        fun <T : ViewBinding> from(
            fragment: Fragment,
            viewBindingBind: (View) -> T,
        ): FragmentViewBindingDelegate<T> = FragmentViewBindingDelegate(
            fragment = fragment,
            viewBindingBind = viewBindingBind,
        )

        fun <T : ViewBinding> from(
            fragment: Fragment,
            viewBindingClazz: Class<T>,
        ): FragmentViewBindingDelegate<T> = FragmentViewBindingDelegate(
            fragment = fragment,
            viewBindingClazz = viewBindingClazz,
        )
    }
}
