package com.zeta.PMS.service;

import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import java.util.List;

public interface PaymentService {

    Payment createPayment(PaymentRequest payment);

    List<Payment> getAllPayments();

    Payment getPaymentById(Long id);

    Payment updatePayment(Long id, PaymentRequest payment);

    void deletePayment(Long id);
}

