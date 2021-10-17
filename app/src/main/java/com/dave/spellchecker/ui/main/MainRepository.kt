package com.dave.spellchecker.ui.main

import com.dave.spellchecker.api.SpellCheckerAPI
import com.dave.spellchecker.network.common.IBoundResource
import com.dave.spellchecker.network.common.Mapper
import com.dave.spellchecker.network.common.NetworkBoundResource
import com.dave.spellchecker.network.dto.SpellChecker
import com.dave.spellchecker.network.pojo.SpellCheckerPOJO
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api: SpellCheckerAPI,
) {

    fun spellCheck(query: String): IBoundResource<SpellCheckerPOJO> {
        return object : NetworkBoundResource<SpellCheckerPOJO, SpellChecker>() {
            override suspend fun getApiCallAsync(): Response<SpellChecker> {
                return api.sellCheck(query)
            }

            override suspend fun mapToPOJO(data: SpellChecker): SpellCheckerPOJO? {
                return Mapper.map(data)
            }
        }
    }
}
