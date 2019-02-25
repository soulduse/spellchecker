package com.dave.spellchecker.api

import com.dave.network.ApiProvider
import com.dave.spellchecker.BuildConfig

object ApiProvider {
    private const val NAVER_SPELL_CHECK_URL = "https://m.search.naver.com/p/csearch/ocontent/util/"

    const val WHERE = "nexearch"
    const val COLOR_BLINDNESS = 0

    fun apiProvider(): SpellCheckerApi = ApiProvider.provideApi(
                service = SpellCheckerApi::class.java,
                baseUrl = NAVER_SPELL_CHECK_URL,
                isDebug = BuildConfig.DEBUG)
}
