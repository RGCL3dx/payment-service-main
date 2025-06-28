package com.programthis.payment_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String orderId; // ID del pedido 
    private BigDecimal amount; // monto
    private String paymentMethod; // tipo de pago
    private String paymentStatus; // proceso de pago
    private LocalDateTime transactionDate;
    private String transactionId; // ID de la transaccion 
 
}