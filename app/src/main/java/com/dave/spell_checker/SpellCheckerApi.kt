package com.dave.spell_checker

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface SpellCheckerApi {

    /**
    .header("X-Timestamp", timestamp)
    .header("X-API-KEY", apiKey)
    .header("X-Customer", customerId.toString())
    .header("X-Signature", Signatures.of(timestamp, request.httpMethod.name, path, secretKey))
     */
    @GET("/keywordstool")
    fun keywordsTool(
        @HeaderMap headers: Map<String, String>,
        @Query("hintKeywords") hintKeywords: String,
        @Query("includeHintKeywords") includeHintKeywords: Int,
        @Query("showDetail") showDetail: Int
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
