package com.where2meet.ui.base.forms

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class FormField<T> {
    protected val mState = MutableStateFlow<T?>(null)

    val state = mState.asStateFlow()

    protected val mIsValid = MutableStateFlow(true)
    val isValid = mIsValid.asStateFlow()

    abstract suspend fun validate(focusIfError: Boolean = true): Boolean

    open fun clearError() {}
    open fun clearFocus() {}
    open fun disable() {}
    open fun enable() {}
}

// helper functions
suspend fun Collection<FormField<*>>.validate(validateAll: Boolean = false): Boolean =
    coroutineScope {
        if (validateAll) {
            map { formField -> async { formField.validate(focusIfError = false) } }
                .awaitAll()
                .all { result -> result }
        } else {
            all { formField -> formField.validate() }
        }
    }

fun Collection<FormField<*>>.clearError() {
    forEach { formField -> formField.clearError() }
}

fun Collection<FormField<*>>.clearFocus() {
    forEach { formField -> formField.clearFocus() }
}

fun Collection<FormField<*>>.disable() {
    forEach { formField -> formField.disable() }
}

fun Collection<FormField<*>>.enable() {
    forEach { formField -> formField.enable() }
}
