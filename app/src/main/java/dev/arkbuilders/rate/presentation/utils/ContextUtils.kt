package dev.arkbuilders.rate.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import dev.arkbuilders.rate.R

fun Context.openLink(
    url: String,
    showToastIfFailed: Boolean = true,
) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    } catch (e: Throwable) {
        if (showToastIfFailed) {
            Toast.makeText(
                this,
                getString(R.string.oops_something_went_wrong),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}

fun Context.openEmail(
    mailTo: String,
    showToastIfFailed: Boolean = true,
) {
    try {
        val uri = Uri.parse("mailto:$mailTo")
        startActivity(Intent(Intent.ACTION_SENDTO, uri))
    } catch (e: Throwable) {
        if (showToastIfFailed) {
            Toast.makeText(
                this,
                getString(R.string.oops_something_went_wrong),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}

fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
