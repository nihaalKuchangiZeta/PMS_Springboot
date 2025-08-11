package com.zeta.PMS.service;

import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.enums.PaymentCategory;
import com.zeta.PMS.enums.PaymentStatus;
import com.zeta.PMS.enums.PaymentType;
import com.zeta.PMS.repository.PaymentRepository;
import com.zeta.PMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import com.zeta.PMS.exception.PaymentCreationException;
import com.zeta.PMS.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentServiceImplementation implements PaymentService {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Payment createPayment(PaymentRequest request) {
        try {
            if (request.getAmount() <= 0) {
                throw new PaymentCreationException("Payment amount must be greater than zero.");
            }

            PaymentType type = ValidationUtil.parseEnumIgnoreCase(PaymentType.class, request.getType());
            PaymentCategory category = ValidationUtil.parseEnumIgnoreCase(PaymentCategory.class, request.getCategory());
            PaymentStatus status = ValidationUtil.parseEnumIgnoreCase(PaymentStatus.class, request.getStatus());

            LocalDate date = ValidationUtil.parseDate(request.getDate());
            if (date.isAfter(LocalDate.now())) {
                throw new PaymentCreationException("Payment date cannot be in the future.");
            }

            if (!userRepository.existsById(request.getCreatedBy())) {
                throw new PaymentCreationException("User ID " + request.getCreatedBy() + "does not exist in the User Table.");
            }

            Payment payment = new Payment();
            payment.setAmount(request.getAmount());
            payment.setType(type);
            payment.setCategory(category);
            payment.setStatus(status);
            payment.setDate(date);
            payment.setUserID(request.getCreatedBy());
            User creator = userRepository.getReferenceById(request.getCreatedBy());
            payment.setCreatedBy(creator);

            return repository.save(payment);

        } catch (PaymentCreationException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentCreationException("Unexpected error occurred while creating payment: " + e.getMessage());
        }
    }


    @Override
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PaymentCreationException("Payment with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public Payment updatePayment(Long id, PaymentRequest updatedRequest) {
        try {
            Payment existingPayment = repository.findById(id)
                    .orElseThrow(() -> new PaymentCreationException("Payment with ID " + id + " not found"));

            // Amount
            if (updatedRequest.getAmount() != null) {
                if (updatedRequest.getAmount() <= 0) {
                    throw new PaymentCreationException("Payment amount must be greater than zero.");
                }
                existingPayment.setAmount(updatedRequest.getAmount());
            }

            // Type
            if (updatedRequest.getType() != null) {
                if (!ValidationUtil.isValidEnumValue(PaymentType.class, updatedRequest.getType())) {
                    throw new PaymentCreationException("Invalid payment type.");
                }
                existingPayment.setType(PaymentType.valueOf(updatedRequest.getType().trim().toUpperCase()));
            }

            // Category
            if (updatedRequest.getCategory() != null) {
                if (!ValidationUtil.isValidEnumValue(PaymentCategory.class, updatedRequest.getCategory())) {
                    throw new PaymentCreationException("Invalid payment category.");
                }
                existingPayment.setCategory(PaymentCategory.valueOf(updatedRequest.getCategory().trim().toUpperCase()));
            }

            // Status
            if (updatedRequest.getStatus() != null) {
                if (!ValidationUtil.isValidEnumValue(PaymentStatus.class, updatedRequest.getStatus())) {
                    throw new PaymentCreationException("Invalid payment status.");
                }
                existingPayment.setStatus(PaymentStatus.valueOf(updatedRequest.getStatus().trim().toUpperCase()));
            }

            // Date
            if (updatedRequest.getDate() != null) {
                if (!ValidationUtil.isValidDate(LocalDate.parse(updatedRequest.getDate()))) {
                    throw new PaymentCreationException("Invalid payment date. Date cannot be null or in the future.");
                }
                existingPayment.setDate(LocalDate.parse(updatedRequest.getDate()));
            }

            // CreatedBy
            if (updatedRequest.getCreatedBy() != null) {
                if (!userRepository.existsById(updatedRequest.getCreatedBy())) {
                    throw new PaymentCreationException("CreatedBy user ID does not exist in the User Table.");
                }
                existingPayment.setUserID(updatedRequest.getCreatedBy());
                existingPayment.setCreatedBy(userRepository.getReferenceById(updatedRequest.getCreatedBy()));
            }

            return repository.save(existingPayment);

        } catch (PaymentCreationException e) {
            throw e; // Known validation issue
        } catch (Exception e) {
            throw new PaymentCreationException("Unexpected error while updating payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        if (!repository.existsById(id)) {
            throw new PaymentCreationException("Payment with ID " + id + " not found");
        }
        repository.deleteById(id);
    }

}
