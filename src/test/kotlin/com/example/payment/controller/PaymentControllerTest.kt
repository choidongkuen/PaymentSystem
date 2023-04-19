package com.example.payment.controller

import com.example.payment.service.PayServiceResponse
import com.example.payment.service.PaymentService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(PaymentController::class)
class PaymentControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockkBean
    private lateinit var paymentService: PaymentService // lateint -> test 에서 많이 사용

    private val mapper = ObjectMapper()

    @Test
    @DisplayName("결제 성공 테스트")
    fun `결제 요청 - 성공 응답`() {
        // given
        every {
            paymentService.pay(any())
        } returns PayServiceResponse(
            paymentUserId = "paymentUserId",
            amount = 2000,
            transactionId = "transactionId",
            transactionAt = LocalDateTime.MIN
        )

        // when

        // then
        mockMvc.post("/api/v1/pay") {
            headers {
                contentType = MediaType.APPLICATION_JSON
                accept = listOf(APPLICATION_JSON)
            } // 헤더
            content = mapper.writeValueAsString(
                PayRequest(
                    paymentUserId = "p1",
                    amount = 100L,
                    merchantTransactionId = "m1",
                    orderName = "orderName"
                )
            ) // 바디
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.paymentUserId", equalTo("paymentUserId")) }
            content { jsonPath("$.amount", equalTo(2000)) }
            content { jsonPath("$.transactionId", equalTo("transactionId")) }
        }.andDo { print() }
    }
}