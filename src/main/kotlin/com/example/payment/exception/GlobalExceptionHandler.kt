package com.example.payment.exception

import mu.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


private val log = KotlinLogging.logger { }

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException::class)
    fun paymentExceptionHandler(
        e: PaymentException
    ): ErrorResponse {
        log.error(e) { "${e.errorCode} occurred." } // 람다식으로 에러 메세지를 받음
        return ErrorResponse(e.errorCode)
    }

    @ExceptionHandler(Exception::class)
    fun exceptionHandler(
        e: Exception
    ): ErrorResponse {
        log.error(e) { "exception occurred." } // 람다식으로 에러 메세지를 받음
        return ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}


class ErrorResponse(
    val errorCode: ErrorCode,
    val errorMessage: String = errorCode.errorMessage
)