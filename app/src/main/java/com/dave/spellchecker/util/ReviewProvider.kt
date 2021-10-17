package com.dave.spellchecker.util

import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewProvider @Inject constructor(
    private val pref: SharedPreferenceProvider
) {

    fun ask(activity: AppCompatActivity, isForce: Boolean = false) {
        if (pref.isFinishedAppReview && !isForce) {
            return
        }

        if (pref.appOpenedCount % 3 != 0 && !isForce) {
            return
        }

        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo).addOnFailureListener {
                    Timber.w("In-app review failed, reason = $it")
                }.addOnCompleteListener {
                    pref.finishAppReview()
                    Timber.d("In-app review finished")
                }
            } else {
                Timber.e("In-app : Error!")
            }
        }
    }
}
