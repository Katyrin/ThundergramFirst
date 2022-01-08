package com.katyrin.freemodule.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import com.katyrin.freemodule.bus.EventBus
import com.katyrin.freemodule.data.Event
import com.katyrin.freemodule.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseActivity : AppCompatActivity() {

    var billingClient: BillingClient? = null

    private val purchasesUpdatedListener: PurchasesUpdatedListener by lazy {
        PurchasesUpdatedListener { billingResult, list ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !list.isNullOrEmpty())
                for (purchase in list) verifyPurchase(purchase)
            else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED)
                toast("error caused by a user cancelling the purchase flow")
            else toast("any other error")
        }
    }

    private fun verifyPurchase(purchase: Purchase) {
        lifecycleScope.launch {
            when (purchase.skus[0]) {
                CALL_50_COIN -> handlePurchase(purchase, COIN_50)
                CALL_100_COIN -> handlePurchase(purchase, COIN_100)
                CALL_500_COIN -> handlePurchase(purchase, COIN_500)
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase, coin: Int) {
        val consumeParams =
            ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        val consumeResult = withContext(Dispatchers.IO) {
            billingClient?.consumePurchase(consumeParams)
        }
        if (consumeResult?.billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            EventBus.invokeEvent(Event(coin))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()
    }

    override fun onResume() {
        super.onResume()
        billingClient?.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, list ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !list.isNullOrEmpty())
                for (purchase in list)
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged)
                        verifyPurchase(purchase)
        }
    }

    override fun onDestroy() {
        billingClient = null
        super.onDestroy()
    }
}