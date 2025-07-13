package com.lcwd.store.controller;

import com.lcwd.store.dtos.PhonepayPayload;
//import com.phonepe.sdk.pg.Env;
//import com.phonepe.sdk.pg.common.http.PhonePeResponse;
//import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
//import com.phonepe.sdk.pg.payments.v1.models.request.PgPayRequest;
//import com.phonepe.sdk.pg.payments.v1.models.response.PayPageInstrumentResponse;
//import com.phonepe.sdk.pg.payments.v1.models.response.PgPayResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PhonepayController {
   private static final String SALT_KEY = "2b9eaec5-6c6b-4928-9267-4a3cea66d770";
   private static final String SALT_INDEX = "1";
   String merchantId = "M22L0ONMXEH8Z";
   String saltKey = "2b9eaec5-6c6b-4928-9267-4a3cea66d770";
   Integer saltIndex = 1;
//   Env env;
   boolean shouldPublishEvents;
//   PhonePePaymentClient phonepeClient;

//   PhonepayController() {
//      this.env = Env.PROD;
//      this.shouldPublishEvents = true;
//      this.phonepeClient = new PhonePePaymentClient(this.merchantId, this.saltKey, this.saltIndex, this.env, this.shouldPublishEvents);
//   }

//   @PostMapping({"/calculateHash"})
//   public ResponseEntity<String> calculateHash(@RequestBody PhonepayPayload phonepayPayload) {
//      String merchantId = "M22L0ONMXEH8Z";
//      String merchantTransactionId = UUID.randomUUID().toString().substring(0, 34);
//      long amount = phonepayPayload.getAmount() * 100L;
//      String merchantUserId = "vaayuskylink@gmail.com";
//      String callbackurl = "https://www.vaayuskylink.com";
//      PgPayRequest pgPayRequest = PgPayRequest.PayPagePayRequestBuilder().amount(amount).merchantId(merchantId).merchantTransactionId(merchantTransactionId).callbackUrl(callbackurl).merchantUserId(merchantUserId).build();
//      PhonePeResponse<PgPayResponse> payResponse = this.phonepeClient.pay(pgPayRequest);
//      PayPageInstrumentResponse payPageInstrumentResponse = (PayPageInstrumentResponse)((PgPayResponse)payResponse.getData()).getInstrumentResponse();
//      String url = payPageInstrumentResponse.getRedirectInfo().getUrl();
//      return ResponseEntity.ok(url);
//   }

   private PaymentRequest createPaymentRequest() {
      PaymentRequest paymentRequest = new PaymentRequest();
      paymentRequest.setMerchantId("M22L0ONMXEH8Z");
      String merchantTransactionId = UUID.randomUUID().toString().substring(0, 34);
      paymentRequest.setMerchantTransactionId(merchantTransactionId);
      paymentRequest.setMerchantUserId("MU933037302229373");
      paymentRequest.setAmount(1.0D);
      paymentRequest.setCallbackUrl("http://http://vaayuskylink.com");
      paymentRequest.setMobileNumber("9056624920");
      DeviceContext deviceContext = new DeviceContext();
      deviceContext.setDeviceOS("WEB");
      paymentRequest.setDeviceContext(deviceContext);
      PaymentInstrument paymentInstrument = new PaymentInstrument();
      paymentInstrument.setType("UPI_INTENT");
      paymentInstrument.setTargetApp("com.phonepe.app");
      paymentRequest.setPaymentInstrument(paymentInstrument);
      return paymentRequest;
   }

   private String calculateSHA256(String input) throws NoSuchAlgorithmException {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
      return this.bytesToHex(hashBytes).toLowerCase();
   }

   private String bytesToHex(byte[] hash) {
      StringBuilder hexString = new StringBuilder(2 * hash.length);
      byte[] var3 = hash;
      int var4 = hash.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte b = var3[var5];
         hexString.append(String.format("%02X", b & 255));
      }

      return hexString.toString();
   }
}
