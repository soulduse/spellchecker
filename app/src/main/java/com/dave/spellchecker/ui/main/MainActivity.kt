package com.dave.spellchecker.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.dave.spellchecker.R
import com.dave.spellchecker.databinding.ActivityMainBinding
import com.dave.spellchecker.ui.ViewBindingActivity
import com.dave.spellchecker.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    private val viewModel by viewModels<MainViewModel>()
    private val clipboard: ClipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    @Inject lateinit var reviewProvider: ReviewProvider
    @Inject lateinit var adProvider: AdProvider

    override fun setup() {
        initAdBanner()
        binding.tvResultGuide.text = getGuide().toHtmlSpanned()
        binding.tvCopy.setOnClickListener { copyResultText() }
        binding.tvCheck.setOnClickListener { spellCheck() }
        binding.etSpellCheck.addTextChangedListener(getTextChanger())
        binding.tvShare.setOnClickListener { shareTextResult() }
        binding.tvRemove.setOnClickListener { resetSpelling() }
        subscribeUI()
        reviewProvider.ask(this)
    }

    private fun shareTextResult() {
        ShareUtil.shareText(this, binding.etResult.text.toString())
    }

    private fun resetSpelling() {
        binding.etSpellCheck.setText("")
        binding.etResult.setText("")
    }

    private fun getTextChanger(): TextWatcher {
        return object: TextWatcher {
            override fun afterTextChanged(p0: Editable) { binding.tvTextSize.text = getString(R.string.text_size, p0.length) }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { binding.tvTextSize.text = getString(R.string.text_size, count) }
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
            ${getGuideText(ResultColors.RED)}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${getGuideText(ResultColors.VIOLET)}<br>
            ${getGuideText(ResultColors.GREEN)}&#160;&#160;&#160;&#160;&#160;&#160;${getGuideText(ResultColors.BLUE)}
        """.trimIndent()
    }

    private fun initAdBanner() {
        adProvider.loadBannerAd(binding.adBanner)
    }
}
