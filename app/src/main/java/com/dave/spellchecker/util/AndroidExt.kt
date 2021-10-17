package com.dave.spellchecker.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.Selection
import android.text.Spanned
import android.util.TypedValue
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment

fun Fragment.getStringSafe(@StringRes res: Int?): String = res?.let { getString(it) } ?: ""

fun Fragment.getStringSafe(@StringRes res: Int?, vararg formatArgs: Any?): String = res?.let {
    if (formatArgs.isNotEmpty()) {
        getString(it, formatArgs)
    } else {
        null
    }
} ?: ""

fun Activity.getStringSafe(@StringRes res: Int?): String = res?.let { getString(it) } ?: ""

fun Activity.getStringSafe(@StringRes res: Int?, vararg formatArgs: Any?): String = res?.let {
    if (formatArgs.isNotEmpty()) {
        getString(it, formatArgs)
    } else {
        null
    }
} ?: ""

fun Context.getStringSafe(@StringRes res: Int?): String = res?.let { getString(it) } ?: ""

fun Context.getStringSafe(@StringRes res: Int?, vararg formatArgs: Any?): String = res?.let {
    if (formatArgs.isNotEmpty()) {
        getString(it, formatArgs)
    } else {
        null
    }
} ?: ""

fun Activity.shareText(text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    this.startActivity(shareIntent)
}

inline fun <reified A : Activity> Context.start(configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, A::class.java).apply(configIntent))
}

inline fun <reified A : Activity> Activity.start(configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, A::class.java).apply(configIntent))
}

inline fun <reified A : Activity> Fragment.start(configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this.context, A::class.java).apply(configIntent))
}

fun Context.getColorSafe(res: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return ContextCompat.getColor(this, res)
    }
    return this.resources.getColor(res)
}

fun Context.getDPInt(value: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    ).toInt()
}

fun Context.getDPFloat(value: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    )
}

fun EditText.initComma(
    listener: ((originNumber: Double) -> Unit)? = null,
) {
    var amount = ""
    this.doOnTextChanged { text, _, _, _ ->
        if (text.isNullOrBlank() || amount == text.toString()) return@doOnTextChanged
        val removeCommaNumberString = text.toString().replace(",", "")
        amount = removeCommaNumberString.toComma()
        listener?.invoke(removeCommaNumberString.toDouble())
        this.setText(amount)
        Selection.setSelection(this.text, amount.length)
    }
}

fun String.toHtmlSpanned(): Spanned {
    val convertedHtml = convertHtml(this)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(convertedHtml, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(convertedHtml)
    }
}

private fun convertHtml(htmlText: String): String {
    return htmlText
        .replace(ResultColors.GREEN.beforeHtml, ResultColors.GREEN.afterHtml)
        .replace(ResultColors.VIOLET.beforeHtml, ResultColors.VIOLET.afterHtml)
        .replace(ResultColors.BLUE.beforeHtml, ResultColors.BLUE.afterHtml)
        .replace(ResultColors.RED.beforeHtml, ResultColors.RED.afterHtml)
        .replace("</em>", "</font>")
}
