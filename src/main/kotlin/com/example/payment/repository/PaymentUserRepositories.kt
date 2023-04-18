package com.example.payment.repository

import com.example.payment.domain.Order
import com.example.payment.domain.OrderTransaction
import com.example.payment.domain.PaymentUser
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentUserRepository : JpaRepository<PaymentUser, Long> {
    fun findByPaymentUserId(payUserId: String): PaymentUser?
}

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByPaymentUser(paymentUser: PaymentUser): Order?
}

interface OrderTransactionRepository : JpaRepository<OrderTransaction, Long> {

}