package com.iceshop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    val orderId: Int = 0,
    val customerId: Int,
    val orderDate: Date = Date(),
    val status: String = "Preparing",
    val totalAmount: Double = 0.0,
    val items: List<OrderItem> = emptyList()
) : Parcelable
