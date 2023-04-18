package com.example.payment.service

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Service
class PaymentService(
    private val paymentStatusService: PaymentStatusService
) {

    fun pay(
        payServiceRequest: PayServiceRequest
    ): PayServiceResponse {

        // 요청을 저장
        val orderId = this.paymentStatusService.savePayRequest(
            paymentUserId = payServiceRequest.paymentUserId,
            amount = payServiceRequest.amount,
            orderName = payServiceRequest.orderName,
            merchantTransactionId = payServiceRequest.merchantTransactionId
        ) // 해당하는 order id 반환

        // 계좌에 금액 사용 요청
        // 성공 : 거래를 성공적으로 저장
        // 실패 : 거래를 실패로 저장

        return PayServiceResponse(
            paymentUserId = "",
            amount = 0,
            transactionId = "",
            transactionAt = LocalDateTime.now()

        )
    }
}

class PayServiceResponse(
    val paymentUserId: String,
    val amount: Long,
    val transactionId: String,
    val transactionAt: LocalDateTime

)

data class PayServiceRequest(
    @field:NotBlank
    val paymentUserId: String, // 결제 사용자
    @field:Min(100)
    val amount: Long, // 결제 금액
    @field:NotBlank
    val merchantTransactionId: String, // 가맹점 거래 번호
    @field:NotBlank
    val orderName: String // 주문 이름
)

