package com.iceshop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val orderItemId: Int = 0,
    val orderId: Int = 0,
    val productId: Int,
    val productName: String = "",
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
) : Parcelable
