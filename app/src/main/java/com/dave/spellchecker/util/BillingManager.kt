package com.dave.spellchecker.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pref: SharedPreferenceProvider,
) {
    private var productDetailsList: List<ProductDetails> = mutableListOf()
    private var productDetails: ProductDetails? = null
    private var isLoadedListener: (() -> Unit)? = null
    private val billingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        CoroutineScope(Dispatchers.IO).launch {
                            handlePurchase(purchase)
                        }
                    }
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                pref.hasSubscribing = false
            }

            else -> {
                Timber.d("purchasesUpdatedListener billingResult: $billingResult")
            }
        }
    }

    fun setLoadedListener(listener: () -> Unit): BillingManager {
        isLoadedListener = listener
        return this
    }

    private fun handlePurchase(purchase: Purchase) {
        val hasSubscribing = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        Timber.d("handlePurchase hasSubscribing: $hasSubscribing")
        pref.hasSubscribing = hasSubscribing

        if (hasSubscribing && !purchase.isAcknowledged) {
            acknowledgePurchase(purchase)
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Timber.e("구독 인증에 실패했습니다: ${billingResult.debugMessage}")
                // 필요하다면 사용자에게 알림을 주거나 다른 처리를 할 수 있습니다.
            } else {
                Timber.d("acknowledgePurchase billingResult: $billingResult")
            }
        }
    }

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    return
                }
                queryProductDetails(BILLING_PRODUCTS)
            }

            override fun onBillingServiceDisconnected() {
                Timber.d("onBillingServiceDisconnected")
            }
        })
    }

    fun queryProductDetails(productIds: List<String>) {
        val queryProductDetailsParams = createQueryProductDetailParams(
            productIds = productIds,
            productType = BillingClient.ProductType.SUBS,
        )

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { _, productDetailsList ->
            this.productDetailsList = productDetailsList
            this.productDetails = productDetailsList.firstOrNull()
            isLoadedListener?.invoke()
            val groupedByProductId = productDetailsList.groupBy { it.productId }
            val billingItems = generateBillingItems(groupedByProductId)
            pref.billingItems = billingItems
            updatePurchases()
        }
    }

    private fun generateBillingItems(groupedByProductId: Map<String, List<ProductDetails>>): List<BillingItem> {
        val billingItems = mutableListOf<BillingItem>()
        for ((productId, detailsList) in groupedByProductId) {
            val items = createBillingItemsFromDetails(productId, detailsList)
            billingItems.addAll(items)
        }
        return billingItems
    }

    private fun createBillingItemsFromDetails(
        productId: String,
        detailsList: List<ProductDetails>,
    ): List<BillingItem> {
        return detailsList.flatMap { productDetails ->
            productDetails.subscriptionOfferDetails?.toList() ?: emptyList()
        }.mapNotNull { details ->
            val purchase = details.pricingPhases.pricingPhaseList.first()
            BillingItem(
                productId = productId,
                token = details.offerToken,
                planId = details.basePlanId,
                billingPeriod = BillingPeriod.valueOf(purchase.billingPeriod),
                price = purchase.formattedPrice,
                priceAmountMicros = purchase.priceAmountMicros,
            )
        }
    }

    private fun createQueryProductDetailParams(
        productIds: List<String>,
        productType: String,
    ) = QueryProductDetailsParams.newBuilder()
        .setProductList(
            productIds.map { productId ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build()
            },
        )
        .build()

    fun endConnection() {
        billingClient.endConnection()
    }

    fun launchBillingFlow(
        activity: Activity,
        offerToken: String,
        error: () -> Unit,
    ) {
        if (this.productDetails == null) {
            error()
            return
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(this.productDetails!!)
                .setOfferToken(offerToken)
                .build(),
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun updatePurchases() {
        val query = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(query) { billingResult, purchases ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK || purchases.isEmpty()) {
                pref.hasSubscribing = false
                return@queryPurchasesAsync
            }
            purchases.forEach { purchase ->
                handlePurchase(purchase)
            }
        }
    }

    companion object {
        val BILLING_PRODUCTS = listOf("pro_version")
    }
}

data class BillingItem(
    val productId: String,
    val token: String,
    val planId: String,
    val billingPeriod: BillingPeriod,
    val price: String,
    val priceAmountMicros: Long,
)

enum class BillingPeriod {
    P1W, P1M, P1Y
}
