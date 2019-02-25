package com.dave.spellchecker.api

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface SpellCheckerApi {
    @GET("SpellerProxy")
    fun sellCheck(
        @Query("q") query: String,
        @Query("where") where: String = ApiProvider.WHERE,
        @Query("color_blindness") colorBlindness: Int = ApiProvider.COLOR_BLINDNESS
    ): Deferred<SpellChecker>
}

data class SpellChecker(
    val message: SpellMessage
)

data class SpellMessage(
    val result: SpellResult
)

data class SpellResult(
    @SerializedName("errata_count")
    val errataCount: Int,
    @SerializedName("origin_html")
    val originHtml: String,
    @SerializedName("html")
    val html: String,
    @SerializedName("notag_html")
    val notagHtml: String
)
