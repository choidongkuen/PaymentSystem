package com.example.payment.service

import com.example.payment.adpater.AccountAdapter
import com.example.payment.adpater.UseBalanceRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest // 통합 테스트(외부 시스템(Account System) 을 호출하기 때문)
class AccountAdapterTest @Autowired constructor(
    private val accountAdapter: AccountAdapter
) {
    @Test
    @DisplayName("게좌 사용 성공 테스트")
    fun `계좌 사용 - 성공 응답`() {
        // given
        val useBalanceRequest = UseBalanceRequest(
            userId = 1L,
            accountNumber = "1000000000",
            amount = 1000
        )

        // when
        val useBalanceResponse = this.accountAdapter.useAccount(
            useBalanceRequest
        )

        // then
        println(useBalanceResponse)
    }
}