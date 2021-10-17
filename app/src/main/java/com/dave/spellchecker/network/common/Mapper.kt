package com.dave.spellchecker.network.common

import com.dave.spellchecker.network.dto.SpellChecker
import com.dave.spellchecker.network.pojo.SpellCheckerPOJO

object Mapper {

    fun map(data: SpellChecker): SpellCheckerPOJO {
        return data.toPOJOItem()
    }

}
