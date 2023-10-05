package com.dave.spellchecker.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.dave.spellchecker.AppData
import com.dave.spellchecker.databinding.ActivitySplashBinding
import com.dave.spellchecker.ui.main.MainActivity
import com.dave.spellchecker.util.AdProvider
import com.dave.spellchecker.util.start
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : ViewBindingActivity<ActivitySplashBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivitySplashBinding =
        ActivitySplashBinding::inflate

    @Inject
    lateinit var adProvider: AdProvider

    override fun setup() {
        initSplashView()
        initAd()
    }

    override fun initViews() {
        // Do something
    }

    private fun initSplashView() {
        when (AppData.APP_ID) {
            15L -> {
                binding.clContainer.setBackgroundColor(Color.parseColor("#DDF4FF"))
                binding.ivAppIcon.visibility = View.VISIBLE
                binding.tvAppName.visibility = View.GONE
                binding.tvBackgroundText.visibility = View.GONE
            }

            16L -> {
                binding.clContainer.setBackgroundColor(Color.BLACK)
                binding.ivAppIcon.visibility = View.GONE
                binding.tvAppName.visibility = View.VISIBLE
                binding.tvBackgroundText.visibility = View.GONE
            }

            17L -> {
                binding.clContainer.setBackgroundColor(Color.parseColor("#B992F1"))
                binding.ivAppIcon.visibility = View.GONE
                binding.tvAppName.visibility = View.GONE
                binding.tvBackgroundText.visibility = View.VISIBLE
            }

            else -> {
                binding.ivAppIcon.visibility = View.VISIBLE
                binding.tvAppName.visibility = View.GONE
                binding.tvBackgroundText.visibility = View.GONE
            }
        }
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
