package com.zeta.PMS.controller;

import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import com.zeta.PMS.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest, @RequestHeader("Authorization") String authHeader) {
        log.info("Received request to create payment: {}", paymentRequest);
        Payment createdPayment = service.createPayment(paymentRequest, authHeader);
        log.info("Payment created successfully with ID: {}", createdPayment.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> payments = service.getAllPayments();
        log.info("Found {} payments", payments.size());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = service.getPaymentById(id);
        log.info("Payment found: {}", payment);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody PaymentRequest paymentRequest, @RequestHeader("Authorization") String authHeader) {
        log.info("Updating payment with ID: {}", id);
        Payment updatedPayment = service.updatePayment(id, paymentRequest, authHeader);
        log.info("Payment with ID {} updated successfully", id);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        log.info("Deleting payment with ID: {}", id);
        String message = service.deletePayment(id);
        log.info(message);
        return ResponseEntity.ok(message);
    }
}


