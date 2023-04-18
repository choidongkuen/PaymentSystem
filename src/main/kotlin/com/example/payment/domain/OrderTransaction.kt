package com.example.payment.domain

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
class OrderTransaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    val order: Order,

    @Column(name = "transaction_id")
    val transactionId: String,

    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,

    @Enumerated(EnumType.STRING)
    val transactionStatus: TransactionStatus,

    @Column(name = "transaction_amount")
    val transactionAmount: Long,

    @Column(name = "merchant_transaction_id")
    val merchantTransactionId: String,

    @Column(name = "pay_method_transaction_id")
    var payMethodTransactionId: String? = null, // 결제 방법 거래 아이디를 추후에 결정

    @Column(name = "failure_code")
    var failureCode: String? = null, // 실패시에만 초기화

    @Column(name = "transaction_at")
    var transactionAt: LocalDateTime? = null, // 거래 시간 추후애 결정

    @Column(name = "description")
    var description: String? = null // 비고 추후에 결정

) : BaseEntity()