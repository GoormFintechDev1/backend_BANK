package com.example.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseDTO {
    private String paymentKey; // 결제 키
    private String orderId; // 주문 ID
    private String orderName; // 주문 이름
    private String requestedAt; // 결제 요청 시간
    private String approvedAt; // 결제 승인 시간
    private String provider; // 간편결제 제공자
    private Long easyPayAmount; // 간편결제 금액
    private Long easyPayDiscountAmount; // 간편결제 할인 금액
    private String currency; // 통화 정보
    private Long totalAmount; // 총 결제 금액
    private Long vat; // 부가세
    private String method; // 결제 방식 (ex. 카드, 간편결제 등)
    private String failReason;

    private String mId; // Merchant ID

}
