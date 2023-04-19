package com.example.payment.domain

enum class OrderStatus {
    CREATED, // 주문 생성
    FAILED, // 주문 실패
    PAID, // 주문 지불 완료
    CANCELED, // 주문 취소
    PARTIAL_REFUNDED, // 부분 환불
    REFUNDED // 환불
}

enum class TransactionType {
    PAYMENT, REFUND, CANCEL
}

enum class TransactionStatus {
    RESERVED, SUCCESS, FAILURE
}