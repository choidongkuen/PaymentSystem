package com.example.payment.util

import java.util.*

fun generateOrderId() = "PO" + getUUID()

fun generateTransactionId() = "PT" + getUUID()

private fun getUUID() = UUID.randomUUID().toString().replace("-", "")