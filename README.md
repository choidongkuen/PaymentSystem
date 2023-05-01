# Payment System(결제 시스템) 구현 해보기

### ☝ 프로젝트 목표 : Kotlin 을 이용해 간단한 결제 시스템을 구현해본다.
### 🔥 프로젝트 요약 : [계좌 시스템](https://github.com/choidongkuen/AccountSystem) 와 연동하는 `결제 시스템` 을 구현해본다.
### ⏰ 프로젝트 기간 : 2023.04.19 ~ 2023.05.01
<details>
<summary>API</summary>
<div markdown="1">


> 결제 시스템

```
- 결제 요청
POST http://localhost:8081/api/v1/pay
{
  "paymentUserId": "ehdrms6900",
  "amount": 2000,
  "merchantTransactionId": "merchantX",
  "orderName": "아이폰13"
}
```

```
- 결제 응답
{
  "paymentUserId": "endrms6900",
  "amount": 2000,
  "merchantTransactionId": "merchantX",
  "transactionAt": 2023-04-29
}
```
----

```
- 환불 요청
POST http://localhost:8081/api/v1/refund
{
  "transactionId": "zxmn1209",
  "refundId": "thisIsRefundId",
  "refundAmount" 2000,
  "refundReason" "변심으로 인한 환뷸"
}
```

```
- 환불 응답
{
  "refundTransactionId": "xxzz",
  "refundAmount": 2000,
  "refundAt": 2023-04-29
}
```
---
> 계좌 시스템

```
- 게좌 잔액 사용 요청
POST http://localhost:8080/transaction/use
{
  "userId": 1L,
  "accountNumber": "3029017690291",
  "amount": 10000L
}

```

```
- 계좌 잔액 사용 응답
{
  "accountNumber": "3029017690291",
  "transactionResultType": "SUCCESS",
  "amount": 10000L,
  "transactionId": "zxmn1212",
  "transactionAt": 2023-04-29
}
```
---

```
- 계좌 잔액 사용 취소 요청
{
  "transactionId": "zxmn1212",
  "accountNumber": "3029017690291",
  "amount": 10000L
}
```

```
- 계좌 잔액 사용 취소 응답
{
  "accountNumber": "3029017690291",
  "transactionResultType": "SUCCESS",
  "amount": 10000L,
  "transactionId": "zxmn1212",
  "transactionAt": 2023-04-29
}
```
  
</div>
</details>

<details>
<summary>ERD</summary>
<div markdown="1">

![](https://velog.velcdn.com/images/choidongkuen/post/bc7dbf2c-ad8d-4cc1-825e-e2bbc38272fe/image.png)

</div>
</details>

<details>
<summary>예외 처리</summary>
<div markdown="1">

- PaymentException 을 통한 예외 처리

- ErrorCode 표

|이름|설명|
|---|---|
|INVALID_REQUEST| 잘못된 요청입니다. |
|ORDER_NOT_FOUND| 해당하는 원거래를 찾을 수 없습니다.|
|CANNOT_REFUND|환불이 불가능한 상태입니다.|
|CANNOT_CANCEL|취소가 불가능한 상태입니다.|
|EXCEED_REFUNDABLE_AMOUNT|환불 가능한 금액을 초과합니다.|
|PARAMETER_ILLEGAL|잘못된 파라미터 요청입니다.|
|LACK_BALANCE|잔액이 부족합니다.|
|INTERNAL_SERVER_ERROR|서버 오류입니다.|


</div>
</details>
