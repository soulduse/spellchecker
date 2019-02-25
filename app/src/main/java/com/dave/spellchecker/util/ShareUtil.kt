package com.dave.spellchecker.util

import android.content.Context
import android.content.Intent


object ShareUtil {
    fun shareText(context: Context, text: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
        } catch (e: Exception) {
            //e.toString();
        }
    }
}
