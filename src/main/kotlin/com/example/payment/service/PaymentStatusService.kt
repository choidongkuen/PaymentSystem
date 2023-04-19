package com.example.payment.service

import com.example.payment.domain.Order
import com.example.payment.domain.OrderStatus
import com.example.payment.domain.OrderTransaction
import com.example.payment.domain.TransactionStatus.*
import com.example.payment.domain.TransactionType.PAYMENT
import com.example.payment.exception.ErrorCode
import com.example.payment.exception.ErrorCode.*
import com.example.payment.exception.PaymentException
import com.example.payment.repository.OrderRepository
import com.example.payment.repository.OrderTransactionRepository
import com.example.payment.repository.PaymentUserRepository
import com.example.payment.util.generateOrderId
import com.example.payment.util.generateTransactionId
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

/*
 * 결제의 요청 저장, 성공, 실패 저장
 * Order 의 상태 관리
*/
@Service
class PaymentStatusService(
    private val paymentUserRepository: PaymentUserRepository,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository
) {

    @Transactional
    fun savePayRequest(
        paymentUserId: String, // 결제 사용자
        amount: Long, // 금액
        orderName: String, // 주문 이름
        merchantTransactionId: String // 가맹점 아이디
    ): Long {
        // order, orderTransaction 저장
        // order 는 paymentUser 의존
        val paymentUser = this.paymentUserRepository.findByPaymentUserId(
            paymentUserId
        ) ?: throw PaymentException(INVALID_REQUEST, "$paymentUserId 사용자가 존재하지 않습니다.")

        val order = this.orderRepository.save(
            Order(
                orderId = generateOrderId(),
                paymentUser = paymentUser,
                orderName = orderName,
                orderStatus = OrderStatus.CREATED,
                orderAmount = amount,
            )
        ) // 주문 생성 후 저장

        this.orderTransactionRepository.save(
            OrderTransaction(
                order = order,
                transactionId = generateTransactionId(),
                transactionType = PAYMENT,
                transactionStatus = RESERVED,
                transactionAmount = amount,
                merchantTransactionId = merchantTransactionId,
                description = orderName
            )
        ) // 주문 세부 거래 저장

        // orderId 리턴
        return order.id ?: throw PaymentException(INTERNAL_SERVER_ERROR)

    }

    @Transactional
    fun saveAsSuccess(
        orderId: Long, payMethodTransactionId: String?
    ): Pair<String, LocalDateTime> {
        val order = getOrderByOrderId(orderId)
            .apply {
                orderStatus = OrderStatus.PAID
                paidAmount = orderAmount
            }

        // 해당 order 와 PAYMENT transactionType 인 orderTransaction 찾아서 SUCCESS 로 수정
        val orderTransaction =
            getOrderTransactions(order).first().apply {
                transactionStatus = SUCCESS
                this.payMethodTransactionId = payMethodTransactionId
                transactionAt = LocalDateTime.now()
            }

        return Pair(
            orderTransaction.transactionId,
            orderTransaction.transactionAt ?: throw PaymentException(
                INTERNAL_SERVER_ERROR
            )
        )
    }

    fun saveAsFailure(orderId: Long, errorCode: ErrorCode): Unit {
        val order = getOrderByOrderId(orderId)
            .apply {
                orderStatus = OrderStatus.FAILED
            }
        val orderTransaction =
            getOrderTransactions(order).first().apply {
                transactionStatus = FAILURE
                failureCode = errorCode.name
                description = errorCode.errorMessage
            }
    }

    private fun getOrderTransactions(order: Order) =
        this.orderTransactionRepository.findByOrderAndTransactionType(
            order = order,
            transactionType = PAYMENT
        )

    private fun getOrderByOrderId(orderId: Long): Order = this.orderRepository.findById(orderId)
        .orElseThrow { throw PaymentException(ORDER_NOT_FOUND) }
}