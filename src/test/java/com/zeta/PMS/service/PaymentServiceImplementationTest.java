package com.zeta.PMS.service;

import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.enums.PaymentCategory;
import com.zeta.PMS.enums.PaymentStatus;
import com.zeta.PMS.enums.PaymentType;
import com.zeta.PMS.exception.PaymentCreationException;
import com.zeta.PMS.repository.PaymentRepository;
import com.zeta.PMS.repository.UserRepository;
import com.zeta.PMS.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplementationTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PaymentServiceImplementation paymentService;

    private PaymentRequest validRequest;
    private User mockUser;
    private Payment mockPayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validRequest = new PaymentRequest();
        validRequest.setAmount(100.0);
        validRequest.setType("INCOMING");
        validRequest.setCategory("INVOICE");
        validRequest.setStatus("COMPLETED");
        validRequest.setDate(LocalDate.now().toString());

        mockUser = new User();
        mockUser.setId(1L);

        mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setAmount(100.0);
        mockPayment.setType(PaymentType.INCOMING);
        mockPayment.setCategory(PaymentCategory.INVOICE);
        mockPayment.setStatus(PaymentStatus.COMPLETED);
        mockPayment.setDate(LocalDate.now());
    }

    @Test
    void createPayment_ShouldSucceed() {
        when(jwtUtil.getUserId(anyString())).thenReturn(1L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(1L)).thenReturn(mockUser);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        Payment result = paymentService.createPayment(validRequest, "Bearer token");

        assertThat(result.getAmount()).isEqualTo(100.0);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void createPayment_InvalidAmount_ShouldThrow() {
        validRequest.setAmount(0.0);
        assertThatThrownBy(() -> paymentService.createPayment(validRequest, "Bearer token"))
                .isInstanceOf(PaymentCreationException.class);
    }

    @Test
    void createPayment_FutureDate_ShouldThrow() {
        validRequest.setDate(LocalDate.now().plusDays(1).toString());
        assertThatThrownBy(() -> paymentService.createPayment(validRequest, "Bearer token"))
                .isInstanceOf(PaymentCreationException.class);
    }

    @Test
    void createPayment_UserNotFound_ShouldThrow() {
        when(jwtUtil.getUserId(anyString())).thenReturn(1L);
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.createPayment(validRequest, "Bearer token"))
                .isInstanceOf(PaymentCreationException.class);
    }

    @Test
    void updatePayment_ShouldSucceed() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));
        when(jwtUtil.getUserId(anyString())).thenReturn(1L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(1L)).thenReturn(mockUser);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        Payment result = paymentService.updatePayment(1L, validRequest, "Bearer token");

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deletePayment_ShouldSucceed() {
        when(paymentRepository.existsById(1L)).thenReturn(true);

        String result = paymentService.deletePayment(1L);

        assertThat(result).isEqualTo("Payment with id 1 deleted");
        verify(paymentRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllPayments_ShouldReturnList() {
        when(paymentRepository.findAll()).thenReturn(List.of(mockPayment));

        List<Payment> result = paymentService.getAllPayments();

        assertThat(result).hasSize(1);
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(mockPayment));

        Payment result = paymentService.getPaymentById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }
}
