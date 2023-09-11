package com.dave.spellchecker.ui.payment

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.dave.spellchecker.R
import com.dave.spellchecker.databinding.ActivityPaymentBinding
import com.dave.spellchecker.ui.ViewBindingActivity
import com.dave.spellchecker.util.BillingItem
import com.dave.spellchecker.util.BillingManager
import com.dave.spellchecker.util.BillingPeriod
import com.dave.spellchecker.util.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : ViewBindingActivity<ActivityPaymentBinding>() {
    private val viewModel by viewModels<PaymentViewModel>()
    override val bindingInflater: (LayoutInflater) -> ActivityPaymentBinding =
        ActivityPaymentBinding::inflate

    @Inject
    lateinit var billingManager: BillingManager
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishActivity()
        }
    }

    override fun setup() {
        binding.ivClose.setOnClickListener { onNewBackPressed() }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        subscribeUI()
        loadingSwitch(isLoading = true)
        onBackPressedDispatcher.addCallback(this, backPressCallback)
        billingManager
            .setLoadedListener {
                loadingSwitch(isLoading = false)
                // Do something if billing is loaded
            }
            .startConnection()
    }

    private fun loadingSwitch(isLoading: Boolean) {
        if (isLoading) {
            nullableBinding?.loadingOverlay?.visibility = View.VISIBLE
            return
        }
        nullableBinding?.loadingOverlay?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        billingManager.updatePurchases()
    }

    private fun onNewBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun subscribeUI() {
        viewModel.billingItems.observe { initBillingViews() }
    }

    private fun initBillingViews() {
        setProductPrices(productId = BillingManager.BILLING_PRODUCTS.last())
    }

    private fun finishActivity() {
        val resultIntent = Intent()
        if (pref.hasSubscribing) {
            // 결제가 완료되었을 경우
            setResult(Activity.RESULT_OK, resultIntent)
        } else {
            // 결제를 하지 않았거나 실패한 경우
            setResult(Activity.RESULT_CANCELED, resultIntent)
        }
        finish()
    }

    private fun getBillingPeriod(billingItem: BillingItem): CharSequence? =
        when (billingItem.billingPeriod) {
            BillingPeriod.P1W -> getString(R.string.weekly)
            BillingPeriod.P1M -> getString(R.string.monthly)
            BillingPeriod.P1Y -> getString(R.string.yearly)
        }

    private fun setProductPrices(productId: String) {
        val billingItem1M =
            viewModel.getBillingItemForPeriod(productId, BillingPeriod.P1M) ?: return
        val billingItem1Y =
            viewModel.getBillingItemForPeriod(productId, BillingPeriod.P1Y) ?: return

        binding.tvPrice1.visibility = View.GONE
        setSingleProductPrice(billingItem1M, binding.tvPrice2)
        setSingleProductPrice(billingItem1Y, binding.tvPrice, binding.tvPeriod, binding.btnPay)
    }

    private fun setSingleProductPrice(
        billingItem: BillingItem,
        priceTextView: TextView,
        periodTextView: TextView? = null,
        payButton: FrameLayout? = null,
    ) {
        val priceText = if (periodTextView == null) {
            "${getBillingPeriod(billingItem)}\n${billingItem.price}"
        } else {
            billingItem.price
        }
        priceTextView.text = priceText
        periodTextView?.text = getBillingPeriod(billingItem)
        payButton?.setOnClickListener {
            billingManager.launchBillingFlow(this, billingItem.token) {
                toast(R.string.payment_info_unavailable)
            }
        }
        priceTextView.setOnClickListener {
            billingManager.launchBillingFlow(this, billingItem.token) {
                toast(R.string.payment_info_unavailable)
            }
        }
    }
}
