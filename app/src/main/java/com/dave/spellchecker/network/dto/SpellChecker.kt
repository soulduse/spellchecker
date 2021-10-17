package com.dave.spellchecker.network.dto

import com.dave.spellchecker.network.pojo.SpellCheckerPOJO
import com.squareup.moshi.Json

data class SpellChecker(
    val message: SpellMessage
) {
    fun toPOJOItem(): SpellCheckerPOJO {
        return SpellCheckerPOJO(message.result.html)
    }

    data class SpellMessage(
        val result: SpellResult
    )

    data class SpellResult(
        @Json(name ="errata_count")
        val errataCount: Int,
        @Json(name ="origin_html")
        val originHtml: String,
        @Json(name ="html")
        val html: String,
        @Json(name ="notag_html")
        val notagHtml: String
    )
}
