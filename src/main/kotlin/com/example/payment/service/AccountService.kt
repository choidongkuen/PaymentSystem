package com.example.payment.service

import com.example.payment.adpater.AccountAdapter
import com.example.payment.adpater.CancelBalanceRequest
import com.example.payment.adpater.UseBalanceRequest
import com.example.payment.domain.Order
import com.example.payment.domain.TransactionType
import com.example.payment.exception.ErrorCode.INTERNAL_SERVER_ERROR
import com.example.payment.exception.ErrorCode.ORDER_NOT_FOUND
import com.example.payment.exception.PaymentException
import com.example.payment.repository.OrderRepository
import com.example.payment.repository.OrderTransactionRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class AccountService(
    private val accountAdapter: AccountAdapter,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository,
) {
    @Transactional
    fun useAccount(orderId: Long): String? {
        // 계좌 사용 요청 및 처리

        // 1. 주문(Order) 찾기
        val order: Order = this.orderRepository.findById(orderId)
            .orElseThrow {
                throw PaymentException(ORDER_NOT_FOUND, "요청하신 주문 정보가 존재하지 않습니다.")
            }
        // 2. 주문(Order) 를 통해 계좌 사용
        return this.accountAdapter.useAccount(
            UseBalanceRequest(
                userId = order.paymentUser.accountUserId,
                accountNumber = order.paymentUser.accountNumber,
                amount = order.orderAmount
            )
            // 3. 계좌 사용 후 거래 id 반환
        ).transactionId
    }

    @Transactional
    fun cancelUseAccount(refundTransactionId: Long): String {
        // 환불을 위해 저장한 OrderTransaction
        val refundTransaction = this.orderTransactionRepository
            .findById(refundTransactionId)
            .orElseThrow {
                // 클라이언트로부터 받은 refundTransactionId 가 없다? => 말이 안됨
                PaymentException(INTERNAL_SERVER_ERROR)
            }

        val order = refundTransaction.order
        // 지불을 위해 저장한 OrderTransaction
        val paymentTransaction =
            this.orderTransactionRepository.findByOrderAndTransactionType(
                order, TransactionType.PAYMENT
            ).first()

        // refundTransactionId -> orderTransaction -> order -> paymentUser
        return this.accountAdapter.cancelUseAccount(
            CancelBalanceRequest(
                transactionId = paymentTransaction.payMethodTransactionId
                    ?: throw PaymentException(INTERNAL_SERVER_ERROR),
                accountNumber = order.paymentUser.accountNumber,
                amount = refundTransaction.transactionAmount
            )
        ).transactionId
    }
}