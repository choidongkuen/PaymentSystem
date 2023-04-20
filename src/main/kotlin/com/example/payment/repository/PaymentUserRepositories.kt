package com.example.payment.repository

import com.example.payment.domain.Order
import com.example.payment.domain.OrderTransaction
import com.example.payment.domain.PaymentUser
import com.example.payment.domain.TransactionType
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentUserRepository : JpaRepository<PaymentUser, Long> {
    fun findByPaymentUserId(payUserId: String): PaymentUser?
}

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByPaymentUser(paymentUser: PaymentUser): Order?
}

interface OrderTransactionRepository : JpaRepository<OrderTransaction, Long> {
    fun findByOrderAndTransactionType(
        order: Order,
        transactionType: TransactionType
    ): List<OrderTransaction>

    fun findByTransactionId(originalTransactionId: String): OrderTransaction?
}