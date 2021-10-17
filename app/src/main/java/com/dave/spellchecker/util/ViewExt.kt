package com.dave.spellchecker.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.dave.spellchecker.R
import com.dave.spellchecker.network.common.Cause
import com.dave.spellchecker.network.common.Resource

fun Context.toast(@StringRes res: Int?) {
    Toast.makeText(this, this.getStringSafe(res), Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(@StringRes res: Int?) {
    Toast.makeText(this.context, this.getStringSafe(res), Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(@StringRes res: Int?) {
    Toast.makeText(this, this.getStringSafe(res), Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.longToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}

fun Activity.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.longToast(@StringRes res: Int?) {
    Toast.makeText(this.context, this.getStringSafe(res), Toast.LENGTH_LONG).show()
}

fun Activity.longToast(@StringRes res: Int?) {
    Toast.makeText(this, this.getStringSafe(res), Toast.LENGTH_LONG).show()
}

fun getErrorMessage(response: Resource.Error): Message {
    return when (response.cause) {
        Cause.NO_INTERNET_CONNECTION -> Message(R.string.message_error_no_internet)
        else -> Message(R.string.error_message_unexpected_server_response)
    }
}
