package com.programthis.payment_service.controller;

import com.programthis.payment_service.entity.Payment;
import com.programthis.payment_service.service.PaymentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentProcessingService paymentProcessingService;

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentProcessingService.PaymentRequest paymentRequest) {
        Payment payment = paymentProcessingService.processPayment(paymentRequest);
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            return ResponseEntity.ok(payment);
        } else {
            // It's often better to return a specific error code like 400 Bad Request
            // for business logic failures, or a more specific 4xx if applicable.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payment);
        }
    }

    @GetMapping("/status/order/{orderId}")
    public ResponseEntity<Payment> getPaymentStatusByOrderId(@PathVariable String orderId) {
        try {
            Payment payment = paymentProcessingService.getPaymentStatusByOrderId(orderId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/transaction/{transactionId}")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            Payment payment = paymentProcessingService.getPaymentByTransactionId(transactionId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- New CRUD Methods ---

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentProcessingService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        try {
            Payment updatedPayment = paymentProcessingService.updatePayment(id, paymentDetails);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentProcessingService.deletePayment(id);
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}