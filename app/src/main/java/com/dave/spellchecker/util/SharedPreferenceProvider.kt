package com.dave.spellchecker.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceProvider @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    var isFinishedAppReview: Boolean
        get() = pref.getBoolean(PREF_REVIEW_FINISHED, false)
        set(value) = pref.edit { putBoolean(PREF_REVIEW_FINISHED, value) }

    val appOpenedCount: Int
        get() = pref.getInt(PREF_APP_OPENED_COUNT, 0)

    fun finishAppReview() {
        isFinishedAppReview = true
    }

    companion object {
        private const val PREF_APP_OPENED_COUNT = "PREF_APP_OPENED_COUNT"
        private const val PREF_REVIEW_FINISHED = "PREF_REVIEW_FINISHED"
    }
}
