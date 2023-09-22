package com.dave.spellchecker.api

import com.dave.spellchecker.network.dto.SpellChecker
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SpellCheckerAPI {
    @POST("/api/v1/spellCheck")
    suspend fun sellCheck(
        @Body query: SpellCheckerRequest,
    ): Response<SpellChecker>
}

data class SpellCheckerRequest(
    val query: String
)
