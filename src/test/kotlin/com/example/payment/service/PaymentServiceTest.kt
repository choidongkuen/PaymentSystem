package com.example.payment.service

import com.example.payment.exception.ErrorCode.*
import com.example.payment.exception.PaymentException
import com.example.payment.service.payment.PayServiceRequest
import com.example.payment.service.payment.PaymentService
import com.example.payment.service.payment.PaymentStatusService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class PaymentServiceTest {

    @RelaxedMockK // 좀더 유연하 Mocking
    lateinit var paymentStatusService: PaymentStatusService

    @MockK
    lateinit var accountService: AccountService

    @InjectMockKs
    lateinit var paymentService: PaymentService

    @Test
    @DisplayName("SuccessPaymentTest")
    fun `결제 성공`() {

        // given
        val request = PayServiceRequest(
            paymentUserId = "paymentUserId",
            amount = 1000L,
            merchantTransactionId = "merchantTransactionId",
            orderName = "orderName"
        )

        every {
            paymentStatusService.savePayRequest(any(), any(), any(), any())

        } returns 1L

        every {
            accountService.useAccount(any())
        } returns "payMethodTransactionId"

        every {
            paymentStatusService.saveAsSuccess(any(), any())
        } returns Pair("transactionId", LocalDateTime.MIN)


        // when
        val result = this.paymentService.pay(request)

        // then
        result.amount shouldBe 1000L
        result.paymentUserId shouldNotBe "paymentUser"
        result.transactionId shouldBe "transactionId"

        verify(exactly = 0) {
            paymentStatusService.saveAsFailure(any(), any())
        }

        verify(exactly = 1) {
            paymentStatusService.saveAsSuccess(any(), any())
        }
    }

    @Test
    @DisplayName("FailurePaymentTest")
    fun `결제 실패`() {
        // given
        val request = PayServiceRequest(
            paymentUserId = "paymentUserId",
            amount = 1000L,
            merchantTransactionId = "merchantTransactionId",
            orderName = "orderName"
        )

        every {
            paymentStatusService.savePayRequest(any(), any(), any(), any())
        } returns 1L

        every {
            accountService.useAccount(any())
        } throws PaymentException(LACK_BALANCE)

        // when
        val result = shouldThrow<PaymentException> {
            paymentService.pay(request)
        }

        // then
        result.errorCode shouldBe LACK_BALANCE

        verify(exactly = 0) {
            paymentStatusService.saveAsSuccess(any(), any())
        }
        verify(exactly = 1) {
            paymentStatusService.saveAsFailure(any(), any())
        }

    }
}