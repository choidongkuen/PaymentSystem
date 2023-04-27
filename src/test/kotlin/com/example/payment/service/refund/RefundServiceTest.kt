package com.example.payment.service.refund

import com.example.payment.service.AccountService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class RefundServiceTest : BehaviorSpec({

    // @Mockk or @InjectMockKs 어노테이션 동작 x한
    val refundStatusService = mockk<RefundStatusService>(relaxed = true)

    val accountService = mockk<AccountService>(relaxed = true)

    val refundService = RefundService(refundStatusService, accountService)

    Given("환불 요청이 정상적으로 저장됨") {

        val refundServiceRequest: RefundServiceRequest =
            RefundServiceRequest(
                transactionId = "transactionId",
                refundId = "refundId",
                refundAmount = 100L,
                refundReason = "refundReason"
            )

        every {
            refundStatusService.saveRefundRequest(any(), any(), any(), any())
        } returns 1L


        When("계좌 시스템이 정상적으로 환불") {

            every {
                accountService.cancelUseAccount(any())
            } returns "accountTxId"
            every {
                refundStatusService.saveAsSuccess(any(), any())
            } returns Pair("refundTxId", LocalDateTime.MIN)

            val result = refundService.refund(refundServiceRequest)

            Then("트랜잭션 ID, 금액이 응답으로 온다") {
                result.refundTransactionId shouldBe "refundTxId"
                result.refundAmount shouldBe 100L
                result.refundAt shouldBe LocalDateTime.MIN
            }

            Then("saveAsSuccess 호출 saveAsFailure 미호출") {
                verify(exactly = 1) {
                    refundStatusService.saveAsSuccess(any(), any())
                }

                verify(exactly = 0) {
                    refundStatusService.saveAsFailure(any(), any())
                }
            }
        }
    }
})
