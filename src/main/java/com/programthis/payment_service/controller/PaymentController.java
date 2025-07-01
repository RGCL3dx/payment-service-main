package com.programthis.payment_service.controller;

import com.programthis.payment_service.entity.Payment;
import com.programthis.payment_service.service.PaymentProcessingService;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentProcessingService paymentProcessingService;

    @Operation(summary = "Procesa un nuevo pago")
    @PostMapping("/process")
    public ResponseEntity<EntityModel<Payment>> processPayment(@RequestBody PaymentProcessingService.PaymentRequest paymentRequest) {
        Payment payment = paymentProcessingService.processPayment(paymentRequest);
        if ("COMPLETED".equals(payment.getPaymentStatus())) {
            return ResponseEntity.ok(toModel(payment));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(toModel(payment));
        }
    }
    
    @Operation(summary = "Obtiene un pago por ID de orden")
    @GetMapping("/status/order/{orderId}")
    public ResponseEntity<EntityModel<Payment>> getPaymentStatusByOrderId(@PathVariable String orderId) {
        try {
            Payment payment = paymentProcessingService.getPaymentStatusByOrderId(orderId);
            return ResponseEntity.ok(toModel(payment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtiene un pago por ID de transacción")
    @GetMapping("/status/transaction/{transactionId}")
    public ResponseEntity<EntityModel<Payment>> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            Payment payment = paymentProcessingService.getPaymentByTransactionId(transactionId);
            return ResponseEntity.ok(toModel(payment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtiene todos los pagos")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Payment>>> getAllPayments() {
        List<EntityModel<Payment>> payments = paymentProcessingService.getAllPayments().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        WebMvcLinkBuilder link = linkTo(methodOn(PaymentController.class).getAllPayments());
        return ResponseEntity.ok(CollectionModel.of(payments, link.withSelfRel()));
    }

    @Operation(summary = "Actualiza un pago existente")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Payment>> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        try {
            Payment updatedPayment = paymentProcessingService.updatePayment(id, paymentDetails);
            return ResponseEntity.ok(toModel(updatedPayment));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Elimina un pago")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentProcessingService.deletePayment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

 
    private EntityModel<Payment> toModel(Payment payment) {
        EntityModel<Payment> model = EntityModel.of(payment);
        
        if (payment != null && payment.getOrderId() != null) {
            try {
                WebMvcLinkBuilder selfLink = linkTo(methodOn(PaymentController.class).getPaymentStatusByOrderId(payment.getOrderId()));
                model.add(selfLink.withSelfRel());
            } catch (Exception e) {
  
            }
        }
        
        try {
            WebMvcLinkBuilder allPaymentsLink = linkTo(methodOn(PaymentController.class).getAllPayments());
            model.add(allPaymentsLink.withRel("all-payments"));
        } catch (Exception e) {
  
        }
        
        return model;
    }
}