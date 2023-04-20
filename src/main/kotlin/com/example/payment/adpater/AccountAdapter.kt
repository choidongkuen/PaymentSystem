package com.example.payment.adpater

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDateTime

@FeignClient(
    name = "account-adapter",
    url = "http://localhost:8080"
)
interface AccountAdapter {
    @PostMapping("/transaction/use")
    fun useAccount(
        @RequestBody useBalanceRequest: UseBalanceRequest
    ): UseBalanceResponse

    @PostMapping("/transaction/cancel")
    fun cancelUseAccount(
        @RequestBody cancelBalanceRequest: CancelBalanceRequest
    ): CancelBalanceResponse
} // 스프링이 작동하면서 해당 인터페이스 구현체를 만들어줌

// option + 드래그 -> 범위 드래그

data class CancelBalanceResponse(
    val accountNumber: String,
    val transactionResultType: TransactionResultType,
    val amount: Long,
    val transactionId: String,
    val transactionAt: LocalDateTime
)

data class CancelBalanceRequest(
    val transactionId: String? = null,
    val accountNumber: String,
    val amount: Long
)


data class UseBalanceResponse(

    val accountNumber: String,
    val transactionResultType: TransactionResultType,
    val amount: Long,
    val transactionId: String,
    val transactionAt: LocalDateTime,
)

data class UseBalanceRequest(

    val userId: Long,
    val accountNumber: String,
    val amount: Long,
)

enum class TransactionResultType {
    SUCCESS,
    FAILURE
}

