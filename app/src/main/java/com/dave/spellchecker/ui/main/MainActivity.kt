package com.dave.spellchecker.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import com.dave.spellchecker.AppData
import com.dave.spellchecker.R
import com.dave.spellchecker.databinding.ActivityMainBinding
import com.dave.spellchecker.ui.ViewBindingActivity
import com.dave.spellchecker.util.AdProvider
import com.dave.spellchecker.util.ResultColors
import com.dave.spellchecker.util.ReviewProvider
import com.dave.spellchecker.util.ShareUtil
import com.dave.spellchecker.util.SharedPreferenceProvider
import com.dave.spellchecker.util.toHtmlSpanned
import com.dave.spellchecker.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    override fun setup() {
        initAdBanner()
        binding.viewModel = viewModel
        binding.tvResultGuide.text = getGuide().toHtmlSpanned()
        initEvents()
        binding.lifecycleOwner = this
        subscribeUI()
        sharedPreferenceProvider.appOpened()
        reviewProvider.ask(this)
    }

    override fun initViews() {
        initBackground()
        initLoadingLottieAnimation()
        initEditTextStyle()
        initButtonStyleAndTextColor()
        initGuideContainer()
    }

    private fun initEvents() {
        binding.ivCopy.setOnClickListener { copyResultText() }
        binding.tvCopy.setOnClickListener { copyResultText() }
        binding.ivCheck.setOnClickListener { spellCheck() }
        binding.tvCheck.setOnClickListener { spellCheck() }
        binding.etSpellCheck.addTextChangedListener(getTextChanger())
        binding.ivShare.setOnClickListener { shareTextResult() }
        binding.tvShare.setOnClickListener { shareTextResult() }
        binding.ivDelete.setOnClickListener { resetSpelling() }
        binding.tvRemove.setOnClickListener { resetSpelling() }
    }

    private fun initEditTextStyle() {
        var firstEditTextStyle: Int? = null
        var secondEditTextStyle: Int? = null
        when (AppData.APP_ID) {
            15L -> {
                firstEditTextStyle = R.drawable.border
                secondEditTextStyle = R.drawable.border
            }

            16L -> {
                firstEditTextStyle = R.drawable.chat_my_bubble
                secondEditTextStyle = R.drawable.chat_other_bubble
            }

            17L -> {
                firstEditTextStyle = R.drawable.border
                secondEditTextStyle = R.drawable.border
            }

            else -> {}
        }
        firstEditTextStyle?.let { binding.etSpellCheck.setBackgroundResource(it) }
        secondEditTextStyle?.let { binding.etResult.setBackgroundResource(it) }
    }

    private fun initLoadingLottieAnimation() {
        when (AppData.APP_ID) {
            15L -> binding.loadingView.setAnimation("loading_15.json")
            16L -> binding.loadingView.setAnimation("loading_16.json")
            17L -> binding.loadingView.setAnimation("loading_17.json")
            else -> binding.loadingView.setAnimation("happy_pencil.json")
        }
    }

    private fun initGuideContainer() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.clMain)
        var targetId = R.id.et_result
        when (AppData.APP_ID) {
            15L, 16L, 17L -> {
                binding.tvResultGuide.visibility = View.GONE
                targetId = R.id.ll_result_guide
            }

            else -> {
                binding.tvResultGuide.visibility = View.VISIBLE
                binding.llGuides.visibility = View.GONE
            }
        }
        constraintSet.connect(
            R.id.ll_bottom_buttons,
            ConstraintSet.TOP,
            targetId,
            ConstraintSet.BOTTOM,
            8,
        )
        constraintSet.applyTo(binding.clMain)
    }

    private fun initButtonStyleAndTextColor() {
        when (AppData.APP_ID) {
            16L -> {
                binding.tvTextSize.setTextColor(Color.WHITE)
                iconSwitch(true)
            }

            17L -> {
                binding.llGuides.setBackgroundColor(Color.parseColor("#66000000"))
                binding.rlMain.setBackgroundColor(Color.parseColor("#B992F1"))
            }

            else -> {}
        }
    }

    private fun iconSwitch(isIconVisible: Boolean) {
        if (isIconVisible) {
            binding.ivDelete.visibility = View.VISIBLE
            binding.ivCheck.visibility = View.VISIBLE
            binding.ivShare.visibility = View.VISIBLE
            binding.ivCopy.visibility = View.VISIBLE
            binding.tvCheck.visibility = View.GONE
            binding.tvRemove.visibility = View.GONE
            binding.tvCopy.visibility = View.GONE
            binding.tvShare.visibility = View.GONE
        } else {
            binding.ivDelete.visibility = View.GONE
            binding.ivCheck.visibility = View.GONE
            binding.ivShare.visibility = View.GONE
            binding.ivCopy.visibility = View.GONE
            binding.tvCheck.visibility = View.VISIBLE
            binding.tvRemove.visibility = View.VISIBLE
            binding.tvCopy.visibility = View.VISIBLE
            binding.tvShare.visibility = View.VISIBLE
        }
    }

    private fun initBackground() {
        when (AppData.APP_ID) {
            16L -> binding.rlMain.setBackgroundColor(Color.BLACK)
            17L -> {
                binding.llGuides.setBackgroundColor(Color.parseColor("#66000000"))
                binding.rlMain.setBackgroundColor(Color.parseColor("#B992F1"))
            }

            else -> {}
        }
    }

    private fun shareTextResult() {
        ShareUtil.shareText(this, binding.etResult.text.toString())
    }

    private fun resetSpelling() {
        binding.etSpellCheck.setText("")
        binding.etResult.setText("")
    }

    private fun getTextChanger(): TextWatcher {
        val textId = R.string.text_size_normal
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
        viewModel.finish.observe { isFinish -> binding.tvCheck.isEnabled = isFinish }
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
                ResultColors.VIOLET,
            )
        }<br>
            ${getGuideText(ResultColors.GREEN)}&#160;&#160;&#160;&#160;&#160;&#160;${
            getGuideText(
                ResultColors.BLUE,
            )
        }
        """.trimIndent()
    }

    override fun onResume() {
        super.onResume()
        if (pref.hasSubscribing) {
            binding.adBanner.visibility = View.GONE
            binding.etSpellCheck.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2000))
        }
    }

    private fun initAdBanner() {
//        if (pref.hasSubscribing) return
//        adProvider.loadBannerAd(binding.adBanner)
    }
}
