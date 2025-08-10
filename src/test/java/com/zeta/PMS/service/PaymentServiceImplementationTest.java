package com.zeta.PMS.service;

import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import com.zeta.PMS.enums.PaymentCategory;
import com.zeta.PMS.enums.PaymentStatus;
import com.zeta.PMS.enums.PaymentType;
import com.zeta.PMS.exception.PaymentCreationException;
import com.zeta.PMS.repository.PaymentRepository;
import com.zeta.PMS.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceImplementationTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImplementation service;

    private PaymentRequest validRequest;
    private Payment paymentEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRequest = new PaymentRequest();
        validRequest.setAmount(1000.0);
        validRequest.setType("INCOMING");
        validRequest.setCategory("SALARY");
        validRequest.setStatus("PENDING");
        validRequest.setDate(LocalDate.now().toString());
        validRequest.setCreatedBy(1L);

        paymentEntity = new Payment();
        paymentEntity.setId(1L);
        paymentEntity.setAmount(1000.0);
        paymentEntity.setType(PaymentType.INCOMING);
        paymentEntity.setCategory(PaymentCategory.SALARY);
        paymentEntity.setStatus(PaymentStatus.PENDING);
        paymentEntity.setDate(LocalDate.now());
        paymentEntity.setCreatedBy(1L);
    }

    @Test
    void testCreatePayment_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);

        Payment created = service.createPayment(validRequest);

        assertNotNull(created);
        assertEquals(1000.0, created.getAmount());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_InvalidAmount_ThrowsException() {
        validRequest.setAmount(0.0);
        assertThrows(PaymentCreationException.class, () -> service.createPayment(validRequest));
    }

    @Test
    void testGetAllPayments_ReturnsList() {
        when(paymentRepository.findAll()).thenReturn(List.of(paymentEntity));
        List<Payment> payments = service.getAllPayments();
        assertEquals(1, payments.size());
    }

    @Test
    void testGetPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PaymentCreationException.class, () -> service.getPaymentById(99L));
    }

    @Test
    void testUpdatePayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentEntity));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);

        validRequest.setAmount(2000.0);
        Payment updated = service.updatePayment(1L, validRequest);

        assertEquals(2000.0, updated.getAmount());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testDeletePayment_NotFound_ThrowsException() {
        when(paymentRepository.existsById(99L)).thenReturn(false);
        assertThrows(PaymentCreationException.class, () -> service.deletePayment(99L));
    }
}
