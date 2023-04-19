package com.example.payment.service

import com.example.payment.adpater.AccountAdapter
import com.example.payment.adpater.UseBalanceRequest
import com.example.payment.domain.Order
import com.example.payment.exception.ErrorCode
import com.example.payment.exception.PaymentException
import com.example.payment.repository.OrderRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class AccountService(
    private val accountAdapter: AccountAdapter,
    private val orderRepository: OrderRepository
) {
    @Transactional
    fun useAccount(orderId: Long): String? {
        // 계좌 사용 요청 및 처리

        // 1. 주문(Order) 찾기
        val order: Order = this.orderRepository.findById(orderId)
            .orElseThrow {
                throw PaymentException(ErrorCode.ORDER_NOT_FOUND, "요청하신 주문 정보가 존재하지 않습니다.")
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
}