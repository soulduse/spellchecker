package com.dave.spellchecker.api

import com.dave.spellchecker.network.dto.SpellChecker
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpellCheckerAPI {
    @GET("/p/csearch/ocontent/util/SpellerProxy")
    suspend fun sellCheck(
        @Query("q") query: String,
        @Query("where") where: String = WHERE,
        @Query("color_blindness") colorBlindness: Int = COLOR_BLINDNESS
    ): Response<SpellChecker>

    companion object {
        private const val WHERE = "nexearch"
        private const val COLOR_BLINDNESS = 0
    }
}

