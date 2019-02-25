package com.dave.spellchecker.util

import android.content.Context
import android.widget.RelativeLayout
import com.dave.spellchecker.BuildConfig
import com.dave.spellchecker.R
import com.google.android.gms.ads.*

class AdProvider private constructor(private val context: Context) {
    private lateinit var mInterstitialAd: InterstitialAd
    private var goToMainListener: (() -> Unit)? = null
    private lateinit var adRequest: AdRequest

    fun init(): AdProvider {
        MobileAds.initialize(context, context.getString(R.string.admob_id))
        adRequest = AdRequest.Builder().build()
        mInterstitialAd = InterstitialAd(context).apply {
            adUnitId = TEST_INTERSTITIAL_ID or context.getString(R.string.admob_interstitial_id)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    showAd()
                }

                override fun onAdClosed() {
                    DLog.w("admob onAdClosed !!")
                    goToMainListener?.invoke()
                }

                override fun onAdFailedToLoad(p0: Int) {
                    DLog.w("admob onAdFailedToLoad !! $p0")
                    goToMainListener?.invoke()
                }
            }
        }
        return this
    }

    fun loadInterstitialAd(): AdProvider {
        mInterstitialAd.loadAd(adRequest)
        return this
    }

    fun loadBannerAd(bannerContainerView: RelativeLayout) {
        val bannerAd = initBanner()
        bannerContainerView.addView(bannerAd)
    }

    private fun initBanner(): AdView = AdView(context).apply {
        adSize = AdSize.SMART_BANNER
        adUnitId = TEST_BANNER_ID or context.getString(R.string.admob_banner_id)
        loadAd(adRequest)
    }

    fun listener(goToMainListener: (() -> Unit)?): AdProvider {
        this.goToMainListener = goToMainListener
        return this
    }

    private fun showAd() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    private infix fun String.or(that: String): String = if (BuildConfig.DEBUG) this else that

    companion object : SingletonHolder<AdProvider, Context>(::AdProvider) {
        private const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}
