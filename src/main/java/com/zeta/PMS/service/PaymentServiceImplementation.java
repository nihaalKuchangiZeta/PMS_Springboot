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
import com.zeta.PMS.util.ValidationUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentServiceImplementation implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImplementation.class);

    private final PaymentRepository repository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public PaymentServiceImplementation(PaymentRepository repository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    public Payment createPayment(PaymentRequest request, String authHeader) {
        logger.info("Attempting to create payment: {}", request);

        try {
            if (request.getAmount() <= 0) {
                logger.warn("Invalid amount: {}", request.getAmount());
                throw new PaymentCreationException("Payment amount must be greater than zero.");
            }

            PaymentType type = ValidationUtil.parseEnumIgnoreCase(PaymentType.class, request.getType());
            PaymentCategory category = ValidationUtil.parseEnumIgnoreCase(PaymentCategory.class, request.getCategory());
            PaymentStatus status = ValidationUtil.parseEnumIgnoreCase(PaymentStatus.class, request.getStatus());

            LocalDate date = ValidationUtil.parseDate(request.getDate());
            if (date.isAfter(LocalDate.now())) {
                logger.warn("Payment date in future: {}", date);
                throw new PaymentCreationException("Payment date cannot be in the future.");
            }

            String token = authHeader.replace("Bearer ", "").trim();
            Long creatorId = jwtUtil.getUserId(token);

            if (!userRepository.existsById(creatorId)) {
                logger.error("User ID {} does not exist in User table.", creatorId);
                throw new PaymentCreationException("User ID " + creatorId + " does not exist in the User table.");
            }

            User creator = userRepository.getReferenceById(creatorId);

            Payment payment = new Payment();
            payment.setAmount(request.getAmount());
            payment.setType(type);
            payment.setCategory(category);
            payment.setStatus(status);
            payment.setDate(date);
            payment.setUserID(creatorId);
            payment.setCreatedBy(creator);

            Payment savedPayment = repository.save(payment);
            logger.info("Payment created successfully with ID {}", savedPayment.getId());
            return savedPayment;

        } catch (PaymentCreationException e) {
            logger.error("Payment creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating payment", e);
            throw new PaymentCreationException("Unexpected error occurred while creating payment: " + e.getMessage());
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        logger.info("Fetching all payments");
        List<Payment> payments = repository.findAll();
        logger.info("Fetched {} payments", payments.size());
        return payments;
    }

    @Override
    public Payment getPaymentById(Long id) {
        logger.info("Fetching payment by ID {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Payment with ID {} not found", id);
                    return new PaymentCreationException("Payment with ID " + id + " not found");
                });
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Payment updatePayment(Long id, PaymentRequest request, String authHeader) {
        logger.info("Updating payment ID {} with data {}", id, request);

        try {
            Payment existingPayment = repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Payment with ID {} not found", id);
                        return new PaymentCreationException("Payment with ID " + id + " not found");
                    });

            // Amount
            if (request.getAmount() != null) {
                if (request.getAmount() <= 0) {
                    logger.warn("Invalid amount: {}", request.getAmount());
                    throw new PaymentCreationException("Payment amount must be greater than zero.");
                }
                existingPayment.setAmount(request.getAmount());
            }

            // Type
            if (request.getType() != null) {
                PaymentType type = ValidationUtil.parseEnumIgnoreCase(PaymentType.class, request.getType());
                existingPayment.setType(type);
            }

            // Category
            if (request.getCategory() != null) {
                PaymentCategory category = ValidationUtil.parseEnumIgnoreCase(PaymentCategory.class, request.getCategory());
                existingPayment.setCategory(category);
            }

            // Status
            if (request.getStatus() != null) {
                PaymentStatus status = ValidationUtil.parseEnumIgnoreCase(PaymentStatus.class, request.getStatus());
                existingPayment.setStatus(status);
            }

            // Date
            if (request.getDate() != null) {
                LocalDate date = ValidationUtil.parseDate(request.getDate());
                if (date.isAfter(LocalDate.now())) {
                    logger.warn("Payment date in future: {}", date);
                    throw new PaymentCreationException("Payment date cannot be in the future.");
                }
                existingPayment.setDate(date);
            }

            // Always set current authenticated user
            String token = authHeader.replace("Bearer ", "").trim();
            Long currentUserId = jwtUtil.getUserId(token);

            if (!userRepository.existsById(currentUserId)) {
                logger.error("User ID {} does not exist in User table.", currentUserId);
                throw new PaymentCreationException("User ID " + currentUserId + " does not exist in the User table.");
            }

            User currentUser = userRepository.getReferenceById(currentUserId);
            existingPayment.setUserID(currentUserId);
            existingPayment.setCreatedBy(currentUser);

            Payment savedPayment = repository.save(existingPayment);
            logger.info("Payment ID {} updated successfully", savedPayment.getId());
            return savedPayment;

        } catch (PaymentCreationException e) {
            logger.error("Payment update failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while updating payment", e);
            throw new PaymentCreationException("Unexpected error while updating payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePayment(Long id) {
        logger.info("Attempting to delete payment ID {}", id);
        if (!repository.existsById(id)) {
            logger.warn("Payment with ID {} not found", id);
            throw new PaymentCreationException("Payment with ID " + id + " not found");
        }
        repository.deleteById(id);
        logger.info("Payment with ID {} deleted successfully", id);
        return "Payment with id " + id + " deleted";
    }
}
