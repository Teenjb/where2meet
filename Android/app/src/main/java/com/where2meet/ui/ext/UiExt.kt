package com.where2meet.ui.ext

import android.content.Context
import android.content.pm.PackageManager
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun Fragment.createSnackbar(
    message: String,
    duration: Int,
): Snackbar = Snackbar.make(requireView(), message, duration)

fun Fragment.snackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    createSnackbar(message, duration).show()
}

fun Fragment.snackbar(
    message: String,
    anchorView: View,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    createSnackbar(message, duration).apply { setAnchorView(anchorView) }.show()
}

fun Fragment.toast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun toggleAppTheme(value: String) {
    AppCompatDelegate.setDefaultNightMode(
        when (value) {
            AppTheme.LIGHT.name -> AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK.name -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        },
    )
}

fun formatIsoDateString(dateString: String, pattern: String): String =
    DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).format(
        LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME),
    )

fun Fragment.checkPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        requireActivity(),
        permission,
    ) == PackageManager.PERMISSION_GRANTED

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true,
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

@Suppress("MagicNumber")
fun Int.getOrdinal(): String {
    val suffixes = listOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (this % 100) {
        11, 12, 13 -> "${this}th"
        else -> "$this${suffixes[this % 10]}"
    }
}
