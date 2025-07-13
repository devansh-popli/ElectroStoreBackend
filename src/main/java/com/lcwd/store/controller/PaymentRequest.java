package com.lcwd.store.controller;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class PaymentRequest {
   private String merchantId;
   private String merchantTransactionId;
   private String merchantUserId;
   private double amount;
   private String callbackUrl;
   private String mobileNumber;
   private DeviceContext deviceContext;
   private PaymentInstrument paymentInstrument;

}
