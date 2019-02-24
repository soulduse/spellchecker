package com.dave.spell_checker

import com.dave.network.ApiProvider

object ApiProvider {

    val keywordsPath = "/keywordstool"
    val apiKey = "0100000000168e87d534384fc804bd30b45da7e1bbd88c42edaa613a72d878f33bceb92123"
    val secretKey = "AQAAAAAWjofVNDhPyAS9MLRdp+G72Zq4GXIGWX3PA/ejfz+S9Q=="
    val customerId = 1448071L

    private const val NAVER_KEYWORDS_URL = "https://api.naver.com"

    fun apiProvider(): NaverKeywordsApi = ApiProvider.provideApi(
                service = NaverKeywordsApi::class.java,
                baseUrl = NAVER_KEYWORDS_URL,
                isDebug = true)
}
