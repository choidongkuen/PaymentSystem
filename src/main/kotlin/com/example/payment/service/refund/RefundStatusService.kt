package com.example.payment.service.refund

import com.example.payment.domain.Order
import com.example.payment.domain.OrderStatus
import com.example.payment.domain.OrderStatus.*
import com.example.payment.domain.OrderTransaction
import com.example.payment.domain.TransactionStatus.*
import com.example.payment.domain.TransactionType.PAYMENT
import com.example.payment.domain.TransactionType.REFUND
import com.example.payment.exception.ErrorCode
import com.example.payment.exception.ErrorCode.*
import com.example.payment.exception.PaymentException
import com.example.payment.repository.OrderRepository
import com.example.payment.repository.OrderTransactionRepository
import com.example.payment.repository.PaymentUserRepository
import com.example.payment.util.generateTransactionId
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

/*
 * 환불의 요청 저장(RESERVED), 성공, 실패 저장
*/
@Service
class RefundStatusService(
    private val paymentUserRepository: PaymentUserRepository,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository
) {

    @Transactional
    fun saveRefundRequest(
        originalTransactionId: String, // 결제 사용자
        merchantRefundId: String,
        refundAmount: Long,
        refundReason: String,
    ): Long {
        // 결제(Order Transaction) 확인
        // 환불이 가능한지 확인
        // 환불 Transaction 저장

        val orderTransaction =
            this.orderTransactionRepository.findByTransactionId(originalTransactionId)
                ?: throw PaymentException(ORDER_NOT_FOUND)

        val order = orderTransaction.order
        validationRefund(order, refundAmount) // 해당 orderTransaction 에 대해 환불 가능 여부 체크

        // 새로운 orderTransaction 생성
        return this.orderTransactionRepository.save(
            OrderTransaction(
                order = order,
                transactionId = generateTransactionId(),
                transactionType = REFUND,
                transactionStatus = RESERVED, // 아직 성공인지 실패인지 Reserved
                transactionAmount = refundAmount,
                merchantTransactionId = merchantRefundId,
                description = refundReason
            )
        ).id ?: throw PaymentException(INTERNAL_SERVER_ERROR)
    }

    private fun validationRefund(order: Order, refundAmount: Long) {

        // 기존에 PAID,PARTIAL 상태여야 함
        if (order.orderStatus !in listOf(PAID, PARTIAL_REFUNDED)) {
            throw PaymentException(CANNOT_REFUND)
        }

        // 지불된 금액을 기존 환불된 금액 + 이번에 환불된 금액이 초과하면 안됨
        if (order.refundedAmount + refundAmount > order.paidAmount) {
            throw PaymentException(EXCEED_REFUNDABLE_AMOUNT)
        }
    }

    @Transactional
    fun saveAsSuccess(
        refundTransactionId: Long, refundAccountTransactionId: String?
    ): Pair<String, LocalDateTime> {

        // 기존에 RESERVED 저장한 orderTransaction 상태 변경 -> REFUND
        val orderTransaction =
            this.orderTransactionRepository.findById(refundTransactionId)
                .orElseThrow {
                    throw PaymentException(INTERNAL_SERVER_ERROR)
                }.apply {
                    transactionStatus = SUCCESS
                    this.payMethodTransactionId = refundAccountTransactionId
                    this.transactionAt = LocalDateTime.now()
                }

        val order = orderTransaction.order
        // 부분 환불 가능
        val totalRefundedAmount = getTotalRefundedAmount(orderTransaction.order)

        order.apply {
            orderStatus = getNewOrderStatus(this, totalRefundedAmount)
            refundedAmount = totalRefundedAmount
        }
        return Pair(
            orderTransaction.transactionId,
            orderTransaction.transactionAt ?: throw PaymentException(INTERNAL_SERVER_ERROR)
        )
    }

    private fun getNewOrderStatus(order: Order, totalRefundedAmount: Long): OrderStatus =
        if (order.orderAmount == totalRefundedAmount) REFUNDED
        else PARTIAL_REFUNDED

    private fun getTotalRefundedAmount(order: Order) =

    // orderTransaction 중 REFUND 타입중 TransactionStatus 가 SUCCESS 인 것만 선택
    // 선택 후, REFUND,SUCCESS 의 SUM
        // ex 200(S),300(S) -> 500
        this.orderTransactionRepository.findByOrderAndTransactionType(order, REFUND)
            .filter { it -> it.transactionStatus == SUCCESS }
            .sumOf { it.transactionAmount }

    fun saveAsFailure(refundOrderTransactionId: Long, errorCode: ErrorCode): Unit {

        this.orderTransactionRepository.findById(refundOrderTransactionId)
            .orElseThrow { throw PaymentException(INTERNAL_SERVER_ERROR) }
            .apply {
                transactionStatus = FAILURE
                failureCode = errorCode.name // ErrorCode 이름
                description = errorCode.errorMessage // ErrorCode message
            }
    }

    private fun getOrderTransactions(order: Order) =
        this.orderTransactionRepository.findByOrderAndTransactionType(
            order = order,
            transactionType = PAYMENT
        )

    private fun getOrderByOrderId(orderId: Long): Order = this.orderRepository.findById(orderId)
        .orElseThrow { PaymentException(ORDER_NOT_FOUND) }
}