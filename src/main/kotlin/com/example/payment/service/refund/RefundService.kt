package com.example.payment.service.refund

import com.example.payment.exception.ErrorCode
import com.example.payment.exception.PaymentException
import com.example.payment.service.AccountService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RefundService(
    private val refundStatusService: RefundStatusService,
    private val accountService: AccountService,
) {

    // 1. 환불 요청 저장(Reserved)
    // 2. 계좌 환불 요청 보내기
    // 3. 성공 or 실패 -> 구체적인 정보 저장
    fun refund(
        refundServiceRequest: RefundServiceRequest
    ): RefundServiceResponse {

        // 요청을 저장
        // refundOrderTransactionId => 환불을 위해 저장한 OrderTransaction 아이디
        val refundOrderTransactionId = this.refundStatusService.saveRefundRequest(
            originalTransactionId = refundServiceRequest.transactionId,
            merchantRefundId = refundServiceRequest.refundId,
            refundAmount = refundServiceRequest.refundAmount,
            refundReason = refundServiceRequest.refundReason
        ) // 해당하는 order id 반환

        return try {
            // 계좌에 금액 사용 취소 요청(외부 시스템에 요청을 보내야함)
            // refundAccountTransactionId => 계좌 사용자의 계좌 Transaction 아이디
            val refundAccountTransactionId = this.accountService.cancelUseAccount(refundOrderTransactionId)

            // 성공 : 거래를 성공적으로 저장
            // pair destructing declaration
            val (transactionId, transactionAt) =
                this.refundStatusService.saveAsSuccess(refundOrderTransactionId, refundAccountTransactionId)

            RefundServiceResponse(
                refundTransactionId = transactionId,
                refundAmount = refundServiceRequest.refundAmount,
                refundAt = transactionAt
            )
        } catch (e: Exception) {
            // -- 실패 : 거래를 실패로 저장
            this.refundStatusService.saveAsFailure(refundOrderTransactionId, getErrorCode(e))
            throw e
        }
    }

    private fun getErrorCode(e: Exception) = if (e is PaymentException) e.errorCode
    else ErrorCode.INTERNAL_SERVER_ERROR
}

data class RefundServiceRequest(
    val transactionId: String, // 거래 아이디
    val refundId: String, // 환불 요청 아아디
    val refundAmount: Long, // 환불 요청 금액
    val refundReason: String // 환불 사유
)

class RefundServiceResponse(
    val refundTransactionId: String,
    val refundAmount: Long,
    val refundAt: LocalDateTime
)


