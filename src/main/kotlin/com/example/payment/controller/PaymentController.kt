package com.example.payment.controller

import com.example.payment.service.PayServiceRequest
import com.example.payment.service.PayServiceResponse
import com.example.payment.service.PaymentService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@RequestMapping("/api/v1")
@RestController
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/pay")
    fun pay(
        @Valid @RequestBody
        payRequest: PayRequest

    ): PayResponse = PayResponse.from(
        this.paymentService.pay(payRequest.toPayServiceRequest())
    )
}

data class PayRequest(
    @field:NotBlank
    val paymentUserId: String, // 결제 사용자
    @field:Min(100)
    val amount: Long, // 결제 금액
    @field:NotBlank
    val merchantTransactionId: String, // 가맹점 거래 번호
    @field:NotBlank
    val orderName: String // 주문 이름
) {
    fun toPayServiceRequest() = PayServiceRequest(
        paymentUserId = this.paymentUserId,
        amount = this.amount,
        merchantTransactionId = this.merchantTransactionId,
        orderName = this.orderName
    )
}

data class PayResponse(
    val paymentUserId: String,
    val amount: Long,
    val transactionId: String,
    val transactionAt: LocalDateTime
) {
    companion object {
        fun from(payServiceResponse: PayServiceResponse) =
            PayResponse(
                paymentUserId = payServiceResponse.paymentUserId,
                amount = payServiceResponse.amount,
                transactionId = payServiceResponse.transactionId,
                transactionAt = payServiceResponse.transactionAt
            )
    }
}


