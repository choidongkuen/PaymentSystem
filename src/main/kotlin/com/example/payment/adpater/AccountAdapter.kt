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
    fun userAccount(
        @RequestBody useBalanceRequest: UseBalanceRequest
    ): UseBalanceResponse
} // 스프링이 작동하면서 해당 인터페이스 구현체를 만들어줌

enum class TransactionResultType {
    SUCCESS,

    FAILURE
}

data class UseBalanceResponse(

    val accountNumber: String? = null,
    val transactionResultType: TransactionResultType? = null,
    val amount: Long? = null,
    val transactionId: String? = null,
    val transactionAt: LocalDateTime? = null
) // private 으로 하면 요청이 제대로 안들어감

data class UseBalanceRequest(

    val userId: Long? = null,

    val accountNumber: String? = null,

    val amount: Long? = null
) // private 으로 하면 요청이 제대로 안들어감
