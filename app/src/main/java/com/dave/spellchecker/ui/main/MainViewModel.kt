package com.dave.spellchecker.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.dave.spellchecker.network.common.Resource
import com.dave.spellchecker.util.Message
import com.dave.spellchecker.util.SingleLiveData
import com.dave.spellchecker.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    private val _htmlResult = MutableLiveData<String>()
    private val _toast = SingleLiveData<Message>()
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _finish = SingleLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    val htmlResult: LiveData<String> get() = _htmlResult
    val toast: LiveData<Message> get() = _toast
    val finish: LiveData<Boolean> get() = _finish

    fun spellCheck(query: String) {
        _finish.value = false
        viewModelScope.launch {
            repository.spellCheck(this, query)
                .getLiveData()
                .asFlow()
                .collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            val pojo = response.data
                            _htmlResult.value = pojo.htmlString
                            _finish.value = true
                        }

                        is Resource.Error -> {
                            getErrorMessage(response)
                            _isLoading.value = false
                            _finish.value = true
                        }
                        is Resource.Loading -> _isLoading.value = response.isLoading
                    }
                }
        }
    }
}
