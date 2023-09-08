package com.dave.spellchecker.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dave.spellchecker.util.BillingItem
import com.dave.spellchecker.util.BillingPeriod
import com.dave.spellchecker.util.Message
import com.dave.spellchecker.util.SharedPreferenceProvider
import com.dave.spellchecker.util.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val pref: SharedPreferenceProvider,
) : ViewModel() {

    private val _billingItems = MutableLiveData<List<BillingItem>>()
    private val _error = SingleLiveData<Message>()
    private val _toast = SingleLiveData<Message>()

    val billingItems: LiveData<List<BillingItem>> get() = _billingItems
    val error: LiveData<Message> get() = _error
    val toast: LiveData<Message> get() = _toast

    init {
        _billingItems.value = pref.billingItems
    }

    fun getBillingItemForPeriod(
        productId: String,
        period: BillingPeriod,
    ): BillingItem? {
        return billingItems.value?.find {
            it.productId == productId && it.billingPeriod == period
        }
    }
}
