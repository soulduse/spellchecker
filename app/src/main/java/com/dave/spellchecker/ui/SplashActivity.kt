package com.dave.spellchecker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dave.spellchecker.R
import com.dave.spellchecker.ui.main.MainActivity
import com.dave.spellchecker.util.AdProvider
import com.dave.spellchecker.util.SharedPreferenceProvider
import com.dave.spellchecker.util.start
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var adProvider: AdProvider

    @Inject
    lateinit var pref: SharedPreferenceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!pref.hasSubscribing) {
            initAd()
            return
        }
        goToMain()
    }

    private fun initAd() {
        adProvider.with(this)
            .fallback { goToMain() }
            .dismissCallback { goToMain() }
            .loadInterstitialAd()
            .afterLoaded { adProvider.show() }
    }

    private fun goToMain() {
        start<MainActivity>()
        finish()
    }
}
