package com.programthis.payment_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programthis.payment_service.controller.PaymentController;
import com.programthis.payment_service.entity.Payment;
import com.programthis.payment_service.service.PaymentProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentProcessingService paymentProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Payment payment;
    private PaymentProcessingService.PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setOrderId("ORD-123");
        payment.setAmount(new BigDecimal("150.00"));
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentStatus("COMPLETED");
        payment.setTransactionId("TXN-XYZ");
        payment.setTransactionDate(LocalDateTime.now());

        paymentRequest = new PaymentProcessingService.PaymentRequest(
                "ORD-123", new BigDecimal("150.00"), "card-details");
    }

    @Test
    void processPayment_whenSuccessful_shouldReturnOk() throws Exception {
        when(paymentProcessingService.processPayment(any(PaymentProcessingService.PaymentRequest.class))).thenReturn(payment);

        mockMvc.perform(post("/api/v1/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"));
    }

    @Test
    void processPayment_whenFails_shouldReturnBadRequest() throws Exception {
        payment.setPaymentStatus("FAILED");
        when(paymentProcessingService.processPayment(any(PaymentProcessingService.PaymentRequest.class))).thenReturn(payment);

        mockMvc.perform(post("/api/v1/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.paymentStatus").value("FAILED"));
    }

    @Test
    void getPaymentStatusByOrderId_whenFound_shouldReturnPayment() throws Exception {
        when(paymentProcessingService.getPaymentStatusByOrderId("ORD-123")).thenReturn(payment);

        mockMvc.perform(get("/api/v1/payments/status/order/{orderId}", "ORD-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORD-123"));
    }

    @Test
    void getPaymentStatusByOrderId_whenNotFound_shouldReturnNotFound() throws Exception {
        when(paymentProcessingService.getPaymentStatusByOrderId("ORD-456")).thenThrow(new RuntimeException("Payment not found"));

        mockMvc.perform(get("/api/v1/payments/status/order/{orderId}", "ORD-456"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getPaymentByTransactionId_whenFound_shouldReturnPayment() throws Exception {
        when(paymentProcessingService.getPaymentByTransactionId("TXN-XYZ")).thenReturn(payment);

        mockMvc.perform(get("/api/v1/payments/status/transaction/{transactionId}", "TXN-XYZ"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.transactionId").value("TXN-XYZ"));
    }
    
    @Test
    void getPaymentByTransactionId_whenNotFound_shouldReturnNotFound() throws Exception {
        when(paymentProcessingService.getPaymentByTransactionId("TXN-FAIL")).thenThrow(new RuntimeException("Not Found"));

        mockMvc.perform(get("/api/v1/payments/status/transaction/{transactionId}", "TXN-FAIL"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPayments_shouldReturnPaymentList() throws Exception {
        List<Payment> payments = Collections.singletonList(payment);
        when(paymentProcessingService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.paymentList[0].orderId").value("ORD-123"));
    }

    @Test
    void updatePayment_whenExists_shouldReturnUpdatedPayment() throws Exception {
        when(paymentProcessingService.updatePayment(eq(1L), any(Payment.class))).thenReturn(payment);

        mockMvc.perform(put("/api/v1/payments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORD-123"));
    }

    @Test
    void updatePayment_whenNotFound_shouldReturnNotFound() throws Exception {
        when(paymentProcessingService.updatePayment(eq(99L), any(Payment.class))).thenThrow(new RuntimeException("Not Found"));
        
        mockMvc.perform(put("/api/v1/payments/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePayment_whenExists_shouldReturnNoContent() throws Exception {
        doNothing().when(paymentProcessingService).deletePayment(1L);

        mockMvc.perform(delete("/api/v1/payments/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePayment_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Payment not found")).when(paymentProcessingService).deletePayment(99L);

        mockMvc.perform(delete("/api/v1/payments/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}