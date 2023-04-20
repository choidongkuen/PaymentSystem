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
            this.paymentStatusService.saveAsFailure(orderId, getErrorCode(e))
            throw e
        }
    }

    private fun getErrorCode(e: Exception) = if (e is PaymentException) e.errorCode
    else ErrorCode.INTERNAL_SERVER_ERROR
}

data class RefundServiceRequest(
    val transactionId: String,
    val refundId: String,
    val refundAmount: Long,
    val refundReason: String
)

class RefundServiceResponse(
    val refundTransactionId: String,
    val refundAmount: Long,
    val refundAt: LocalDateTime
)


