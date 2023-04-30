## Payment System 구현 해보기

### ☝ 프로젝트 목표 : Kotlin 을 이용해 간단한 결제 시스템을 구현해본다.

### ⏰ 프로젝트 기간 : 2023.04.19 ~ 

<details>
<summary>API</summary>
<div markdown="1">

```
// 1. 결제 요청
POST http://localhost:8080/api/v1/pay
{
  "paymentUserId": "ehdrms6900",
  "amount": 2000,
  "merchantTransactionId": "merchantX"
  "orderName": "아이폰13"
}
```
```
// 2. 환불 요청
POST http://localhost:8080/api/v1/refund
{
  "transactionId": "zxmn1209",
  "refundId": "thisIsRefundId",
  "refundAmount" "2000",
  "refundReason" "변심으로 인한 환뷸"
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


</div>
</details>
