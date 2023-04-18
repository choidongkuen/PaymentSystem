package com.example.payment.service

import com.example.payment.domain.Order
import com.example.payment.domain.OrderStatus
import com.example.payment.domain.OrderTransaction
import com.example.payment.domain.TransactionStatus.RESERVED
import com.example.payment.domain.TransactionType.PAYMENT
import com.example.payment.exception.ErrorCode
import com.example.payment.exception.PaymentException
import com.example.payment.repository.OrderRepository
import com.example.payment.repository.OrderTransactionRepository
import com.example.payment.repository.PaymentUserRepository
import com.example.payment.util.generateOrderId
import com.example.payment.util.generateTransactionId
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/*
 * 결제의 요청 저장, 성공, 실패 저장
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
        ) ?: throw PaymentException(ErrorCode.INVALID_REQUEST, "$paymentUserId 사용자가 존재하지 않습니다.")

        val order = orderRepository.save(
            Order(
                orderId = generateOrderId(),
                paymentUser = paymentUser,
                orderName = orderName,
                orderStatus = OrderStatus.CREATED,
                orderAmount = amount,
            )
        ) // 주문 생성 후 저장

        orderTransactionRepository.save(
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
        return order.id ?: throw PaymentException(ErrorCode.INTERNAL_SERVER_ERROR)

    }
}