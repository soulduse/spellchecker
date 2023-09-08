package com.dave.spellchecker.ui.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import com.dave.spellchecker.R
import com.dave.spellchecker.databinding.ActivityMainBinding
import com.dave.spellchecker.ui.ViewBindingActivity
import com.dave.spellchecker.ui.payment.PaymentActivity
import com.dave.spellchecker.util.AdProvider
import com.dave.spellchecker.util.ResultColors
import com.dave.spellchecker.util.ReviewProvider
import com.dave.spellchecker.util.ShareUtil
import com.dave.spellchecker.util.SharedPreferenceProvider
import com.dave.spellchecker.util.start
import com.dave.spellchecker.util.toHtmlSpanned
import com.dave.spellchecker.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    private val viewModel by viewModels<MainViewModel>()
    private val clipboard: ClipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    @Inject
    lateinit var sharedPreferenceProvider: SharedPreferenceProvider

    @Inject
    lateinit var reviewProvider: ReviewProvider

    @Inject
    lateinit var adProvider: AdProvider

    private var isAnimating = false
    private var isScrollingDown = true
    private var isVIPButtonVisible = true

    override fun setup() {
        initAdBanner()
        binding.tvResultGuide.text = getGuide().toHtmlSpanned()
        binding.tvCopy.setOnClickListener { copyResultText() }
        binding.tvCheck.setOnClickListener { spellCheck() }
        binding.etSpellCheck.addTextChangedListener(getTextChanger())
        binding.tvShare.setOnClickListener { shareTextResult() }
        binding.tvRemove.setOnClickListener { resetSpelling() }
        binding.vipButton.setOnClickListener { start<PaymentActivity>() }
        subscribeUI()
        sharedPreferenceProvider.appOpened()
        reviewProvider.ask(this)
        controlVipButton()
    }

    private fun shareTextResult() {
        ShareUtil.shareText(this, binding.etResult.text.toString())
    }

    private fun resetSpelling() {
        binding.etSpellCheck.setText("")
        binding.etResult.setText("")
    }

    private fun getTextChanger(): TextWatcher {
        val textId = if (pref.hasSubscribing) {
            R.string.text_size
        } else {
            R.string.text_size_normal
        }
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                binding.tvTextSize.text = getString(textId, p0.length)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvTextSize.text = getString(textId, count)
            }
        }
    }

    private fun spellCheck() {
        val query: String? = binding.etSpellCheck.text.toString()
        if (query.isNullOrEmpty()) {
            toast(R.string.hint_edit_spell)
            return
        }
        viewModel.spellCheck(query)
    }

    private fun subscribeUI() {
        viewModel.htmlResult.observe { result -> binding.etResult.setText(result.toHtmlSpanned()) }
        viewModel.toast.observe { message -> toast(message.messageRes) }
    }

    private fun copyResultText() {
        if (binding.etResult.text.isNullOrEmpty()) {
            toast(R.string.not_copied_text)
            return
        }

        val clip = ClipData.newPlainText("copy lavel", binding.etResult.text.toString())
        clipboard.setPrimaryClip(clip)
        toast(R.string.copied_text)
    }

    private fun getGuideText(resultColor: ResultColors): String {
        return "${resultColor.afterHtml}${resultColor.title}</font>"
    }

    private fun getGuide(): String {
        return """
            ${getGuideText(ResultColors.RED)}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${
        getGuideText(
            ResultColors.VIOLET
        )
        }<br>
            ${getGuideText(ResultColors.GREEN)}&#160;&#160;&#160;&#160;&#160;&#160;${
        getGuideText(
            ResultColors.BLUE
        )
        }
        """.trimIndent()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume @@@@@@@@@")
        if (pref.hasSubscribing) {
            binding.adBanner.visibility = View.GONE
            binding.etSpellCheck.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2000))
        }
    }

    private fun initAdBanner() {
        if (pref.hasSubscribing) return
        adProvider.loadBannerAd(binding.adBanner)
    }

    private fun controlVipButton() {
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                // 아래로 스크롤 중
                if (!isScrollingDown && isVIPButtonVisible) {
                    animateVIPButton(false)
                    isVIPButtonVisible = false
                }
                isScrollingDown = true
            } else {
                // 위로 스크롤 중
                if (isScrollingDown && !isVIPButtonVisible) {
                    animateVIPButton(true)
                    isVIPButtonVisible = true
                }
                isScrollingDown = false
            }
        })
    }

    private fun animateVIPButton(isVisible: Boolean) {
        if (isAnimating) return
        val startX = if (isVisible) binding.vipButton.width.toFloat() else 0f
        val endX = if (isVisible) 0f else binding.vipButton.width.toFloat()
        val animator = ObjectAnimator.ofFloat(binding.vipButton, "translationX", startX, endX)
        animator.duration = 300 // 애니메이션 지속 시간을 설정합니다. 원하는 대로 변경할 수 있습니다.

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
                binding.vipButton.visibility = if (isVisible) View.VISIBLE else View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animator.start()
    }
}
