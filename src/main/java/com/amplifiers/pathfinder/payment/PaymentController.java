package com.amplifiers.pathfinder.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.amplifiers.pathfinder.utility.Variables.ClientSettings;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("api/v1/public/enrollment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PostMapping(value = "/payment-success", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Void> handlePaymentSuccess(
            @RequestParam Map<String, String> payload) {
        System.out.println("success url hit, payload - " + payload);
        boolean paymentValidationSuccessful = service.paymentSuccess(payload);

        String redirectUrl;
        if (paymentValidationSuccessful) {
            redirectUrl = ClientSettings.clientBaseUrl + "payment/success?transactionId=" + payload.get("tran_id");
        } else {
            redirectUrl = ClientSettings.clientBaseUrl + "payment/fail?transactionId=" + payload.get("tran_id");
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    @PostMapping(value = "/payment-cancel", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> handlePaymentCancel(
            @RequestParam Map<String, String> payload) {
        service.paymentCancel(payload);
        System.out.println("cancel url hit, payload - " + payload);
        String redirectUrl = ClientSettings.clientBaseUrl + "payment/cancel?transactionId=" + payload.get("tran_id");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    @PostMapping(value = "/payment-fail", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> handlePaymentFail(
            @RequestParam Map<String, String> payload) {
        service.paymentFail(payload);
        System.out.println("fail url hit, payload - " + payload);
        String redirectUrl = ClientSettings.clientBaseUrl + "payment/fail?transactionId=" + payload.get("tran_id");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }
}
