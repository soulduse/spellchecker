package com.dave.spellchecker.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import com.dave.network.Network
import com.dave.spellchecker.R
import com.dave.spellchecker.api.ApiProvider
import com.dave.spellchecker.util.AdProvider
import com.dave.spellchecker.util.ResultColors
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val adProvider: AdProvider by lazy { AdProvider.getInstance(this) }
    private val clipboard: ClipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdBanner()
        tv_result_guide.text = getHtml(getGuide())

        tv_copy.setOnClickListener { copyResultText() }
        tv_check.setOnClickListener { spellCheck() }
        changeTextSize()
    }

    private fun changeTextSize() {
        et_spell_check.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable) {
                tv_text_size.text = getString(R.string.text_size, p0.length)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tv_text_size.text = getString(R.string.text_size, count)
            }
        })
    }

    private fun spellCheck() {
        val query: String? = et_spell_check.text.toString()
        if (query.isNullOrEmpty()) {
            toast(R.string.hint_edit_spell)
            return
        }

        val call = ApiProvider.apiProvider().sellCheck(query = query!!)
        Network.request(
                call = call,
                success = { et_result.setText(getHtml(it.message.result.html)) },
                error = { longToast(R.string.error_api) }
        )
    }

    private fun getHtml(htmlText: String): Spanned {
        val convertedHtml = convertHtml(htmlText)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(convertedHtml, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(convertedHtml)
        }
    }

    private fun convertHtml(htmlText: String): String {
        return htmlText
            .replace(ResultColors.GREEN.beforeHtml, ResultColors.GREEN.afterHtml)
            .replace(ResultColors.VIOLET.beforeHtml, ResultColors.VIOLET.afterHtml)
            .replace(ResultColors.BLUE.beforeHtml, ResultColors.BLUE.afterHtml)
            .replace(ResultColors.RED.beforeHtml, ResultColors.RED.afterHtml)
            .replace("</em>", "</font>")
    }

    private fun copyResultText() {
        if (et_result.text.isNullOrEmpty()) {
            toast(R.string.not_copied_text)
            return
        }

        val clip = ClipData.newPlainText("copy lavel", et_result.text.toString())
        clipboard.primaryClip = clip
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
        adProvider.loadBannerAd(ad_banner)
    }
}
