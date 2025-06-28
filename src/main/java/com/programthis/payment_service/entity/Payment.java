package com.programthis.payment_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus; // e.g., "COMPLETED", "FAILED", "PENDING"
    private String transactionId;
    private LocalDateTime transactionDate;

    // Constructors
    public Payment() {
    }

    public Payment(String orderId, BigDecimal amount, String paymentMethod, String paymentStatus, String transactionId, LocalDateTime transactionDate) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    // Opcional: toString para debugging
    @Override
    public String toString() {
        return "Payment{" +
               "id=" + id +
               ", orderId='" + orderId + '\'' +
               ", amount=" + amount +
               ", paymentMethod='" + paymentMethod + '\'' +
               ", paymentStatus='" + paymentStatus + '\'' +
               ", transactionId='" + transactionId + '\'' +
               ", transactionDate=" + transactionDate +
               '}';
    }
}