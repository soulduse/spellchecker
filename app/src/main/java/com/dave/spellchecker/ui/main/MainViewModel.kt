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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    private val _htmlResult = MutableLiveData<String>()
    private val _toast = SingleLiveData<Message>()

    val htmlResult: LiveData<String> get() = _htmlResult
    val toast: LiveData<Message> get() = _toast

    fun spellCheck(query: String) {
        viewModelScope.launch {
            when (val response = repository.spellCheck(query).getAsync()) {
                is Resource.Success -> {
                    val pojo = response.data
                    _htmlResult.value = pojo.htmlString
                }
                is Resource.Error -> getErrorMessage(response)
            }
        }
    }
}
