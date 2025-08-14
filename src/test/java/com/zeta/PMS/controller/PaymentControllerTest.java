package com.zeta.PMS.controller;

import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import com.zeta.PMS.enums.PaymentCategory;
import com.zeta.PMS.enums.PaymentStatus;
import com.zeta.PMS.enums.PaymentType;
import com.zeta.PMS.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private Payment payment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(200.0);
        payment.setType(PaymentType.INCOMING);
        payment.setCategory(PaymentCategory.INVOICE);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setDate(LocalDate.of(2025, 8, 10));
        payment.setUserID(101L);
    }

    @Test
    void testCreatePayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(200.0);
        request.setType("INCOMING");
        request.setCategory("INVOICE");
        request.setStatus("COMPLETED");
        request.setDate("2025-08-10");

        when(paymentService.createPayment(any(PaymentRequest.class), anyString())).thenReturn(payment);

        ResponseEntity<?> response = paymentController.createPayment(request, "Bearer token");

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(((Payment) response.getBody()).getAmount()).isEqualTo(200.0);
        verify(paymentService, times(1)).createPayment(any(PaymentRequest.class), anyString());
    }

    @Test
    void testGetAllPayments() {
        when(paymentService.getAllPayments()).thenReturn(Arrays.asList(payment));

        ResponseEntity<List<Payment>> response = paymentController.getAllPayments();

        assertThat(response.getBody()).hasSize(1);
        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void testGetPaymentById() {
        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        ResponseEntity<Payment> response = paymentController.getPaymentById(1L);

        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    void testUpdatePayment() {
        when(paymentService.updatePayment(eq(1L), any(PaymentRequest.class), anyString())).thenReturn(payment);

        ResponseEntity<Payment> response = paymentController.updatePayment(1L, new PaymentRequest(), "Bearer token");

        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(paymentService, times(1)).updatePayment(eq(1L), any(PaymentRequest.class), anyString());
    }

    @Test
    void testDeletePayment() {
        when(paymentService.deletePayment(1L)).thenReturn("Payment with id 1 deleted");

        ResponseEntity<String> response = paymentController.deletePayment(1L);

        assertThat(response.getBody()).isEqualTo("Payment with id 1 deleted");
        verify(paymentService, times(1)).deletePayment(1L);
    }
}
