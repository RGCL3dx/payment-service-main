package com.programthis.payment_service.service;

import com.programthis.payment_service.entity.Payment;
import com.programthis.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentProcessingService {

    @Autowired
    private PaymentRepository paymentRepository;

    public static record PaymentRequest(String orderId, BigDecimal amount, String paymentMethodDetails) {}

    public Payment processPayment(PaymentRequest paymentRequest) {

        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.orderId());
        payment.setAmount(paymentRequest.amount());
        payment.setPaymentMethod("PROCESSED_METHOD"); // Se puede mejorar en una versión futura
        payment.setTransactionDate(LocalDateTime.now());

        boolean paymentSuccessful = simulatePaymentGatewayInteraction(paymentRequest.paymentMethodDetails());

        if (paymentSuccessful) {
            payment.setPaymentStatus("COMPLETED");
            payment.setTransactionId(UUID.randomUUID().toString());
        } else {
            payment.setPaymentStatus("FAILED");
        }
        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentGatewayInteraction(String paymentMethodDetails) {
        // En una app real, aquí se integraría con una pasarela de pago.
        // La simulación hace que falle si los detalles contienen "fail".
        return paymentMethodDetails == null || !paymentMethodDetails.contains("fail");
    }

    public Payment getPaymentStatusByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction ID: " + transactionId));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePayment(Long id, Payment paymentDetails) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    // **AQUÍ ESTÁ LA CORRECCIÓN CLAVE**
                    // Se copian todas las propiedades del objeto de entrada al objeto existente.
                    payment.setOrderId(paymentDetails.getOrderId());
                    payment.setAmount(paymentDetails.getAmount());
                    payment.setPaymentMethod(paymentDetails.getPaymentMethod());
                    payment.setPaymentStatus(paymentDetails.getPaymentStatus());
                    payment.setTransactionId(paymentDetails.getTransactionId());
                    payment.setTransactionDate(paymentDetails.getTransactionDate());
                    return paymentRepository.save(payment);
                })
                .orElseThrow(() -> new RuntimeException("Payment not found for ID: " + id));
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found for ID: " + id);
        }
        paymentRepository.deleteById(id);
    }
}