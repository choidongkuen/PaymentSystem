package com.example.payment.domain

import javax.persistence.*

@Entity
class PaymentUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "PAYMENT_USER_ID")
    val paymentUserId: String,
    @Column(name = "ACCOUNT_USER_ID")
    val accountUserId: Long,
    @Column(name = "ACCOUNT_NUMBER")
    val accountNumber: String,
    @Column(name = "NAME")
    val name: String
) : BaseEntity()

