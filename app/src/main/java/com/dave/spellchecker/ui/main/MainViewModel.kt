package com.dave.spellchecker.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dave.spellchecker.network.common.Resource
import com.dave.spellchecker.util.Message
import com.dave.spellchecker.util.SingleLiveData
import com.dave.spellchecker.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    private val _htmlResult = MutableLiveData<String>()
    private val _toast = SingleLiveData<Message>()

    val htmlResult: LiveData<String> get() = _htmlResult
    val toast: LiveData<Message> get() = _toast

    fun spellCheck(query: String) {
        if (query.length <= 500) {
            viewModelScope.launch { checkSpellForPart(query) }
            return
        }

        val parts = splitQueryIntoParts(query)
        val combinedResult = StringBuilder()
        viewModelScope.launch {
            for (part in parts) {
                when (val response = repository.spellCheck(part).getAsync()) {
                    is Resource.Success -> {
                        val pojo = response.data
                        combinedResult.append(pojo.htmlString)
                    }
                    is Resource.Error -> {
                        getErrorMessage(response)
                        return@launch // 에러 발생 시 나머지 요청을 중단하고 에러 메시지 출력
                    }
                    else -> {}
                }
            }
            _htmlResult.value = combinedResult.toString()
        }
    }

    private fun splitQueryIntoParts(query: String): List<String> {
        val parts = mutableListOf<String>()

        var index = 0
        while (index < query.length) {
            val endIndex = getEndIndex(query, index + PART_SIZE)
            parts.add(query.substring(index, endIndex))
            index = endIndex
        }

        return parts
    }

    private fun getEndIndex(query: String, preferredIndex: Int): Int {
        if (preferredIndex >= query.length) return query.length

        // 띄어쓰기를 기준으로 구분
        var spaceIndex = query.lastIndexOf(" ", startIndex = preferredIndex)
        if (spaceIndex == -1) spaceIndex = preferredIndex

        // 문장 끝 구분자를 기준으로 구분
        val punctuationIndex = query.substring(0, spaceIndex).lastIndexOfAny(listOf(".", "?", "!"))

        return if (punctuationIndex > -1 && punctuationIndex > spaceIndex - 15) {
            punctuationIndex + 1
        } else {
            spaceIndex
        }
    }


    private suspend fun checkSpellForPart(part: String) {
        when (val response = repository.spellCheck(part).getAsync()) {
            is Resource.Success -> {
                val pojo = response.data
                _htmlResult.value = pojo.htmlString
            }
            is Resource.Error -> getErrorMessage(response)
            else -> {}
        }
    }

    companion object {
        private const val PART_SIZE = 500
    }
}
