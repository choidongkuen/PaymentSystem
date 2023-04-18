package com.example.payment.domain

import javax.persistence.*

@Entity(name = "Orders")
class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "order_id")
    val orderId: String,

    @ManyToOne
    @JoinColumn(name = "payment_user_id")
    val paymentUser: PaymentUser,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus,

    @Column(name = "ORDER_NAME")
    val orderName: String,

    @Column(name = "ORDER_AMOUNT")
    val orderAmount: Long, // 고정 값

    @Column(name = "PAID_AMOUNT")
    var paidAmount: Long, // 결제된 금액(변경 가능)

    @Column(name = "REFUNDED_AMOUNT")
    var refundedAmount: Long // 환불된 금액(변경 가능)
) : BaseEntity()
