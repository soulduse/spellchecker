package com.dave.spellchecker.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceProvider @Inject constructor(
    @ApplicationContext context: Context,
    private val moshi: Moshi,
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

    fun appOpened() {
        val openCount = appOpenedCount + 1
        pref.edit { putInt(PREF_APP_OPENED_COUNT, openCount) }
    }

    var hasSubscribing: Boolean
        get() = pref.getBoolean(PREF_HAS_SUBSCRIBING, false)
        set(value) = pref.edit { putBoolean(PREF_HAS_SUBSCRIBING, value) }

    var billingItems: List<BillingItem>
        get() {
            val json = pref.getString(PREF_BILLING_ITEMS, null) ?: return emptyList()
            return moshi.parseList(json) ?: emptyList()
        }
        set(value) = pref.edit {
            val json = moshi.listToJson(value)
            putString(PREF_BILLING_ITEMS, json)
        }

    companion object {
        private const val PREF_APP_OPENED_COUNT = "PREF_APP_OPENED_COUNT"
        private const val PREF_REVIEW_FINISHED = "PREF_REVIEW_FINISHED"
        private const val PREF_HAS_SUBSCRIBING = "PREF_HAS_SUBSCRIBING"
        private const val PREF_BILLING_ITEMS = "PREF_BILLING_ITEMS"
    }
}
