package com.zeta.PMS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.PMS.dto.PaymentRequest;
import com.zeta.PMS.entity.Payment;
import com.zeta.PMS.enums.PaymentCategory;
import com.zeta.PMS.enums.PaymentStatus;
import com.zeta.PMS.enums.PaymentType;
import com.zeta.PMS.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Payment paymentEntity;
    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        paymentEntity = new Payment();
        paymentEntity.setId(1L);
        paymentEntity.setAmount(1000.0);
        paymentEntity.setType(PaymentType.INCOMING);
        paymentEntity.setCategory(PaymentCategory.SALARY);
        paymentEntity.setStatus(PaymentStatus.PENDING);
        paymentEntity.setDate(LocalDate.now());
        paymentEntity.setCreatedBy(1L);

        request = new PaymentRequest();
        request.setAmount(1000.0);
        request.setType("INCOMING");
        request.setCategory("SALARY");
        request.setStatus("PENDING");
        request.setDate(LocalDate.now().toString());
        request.setCreatedBy(1L);
    }

    @Test
    void testCreatePayment_Returns201() throws Exception {
        Mockito.when(service.createPayment(any(PaymentRequest.class))).thenReturn(paymentEntity);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(1000.0));
    }

    @Test
    void testGetAllPayments_ReturnsOk() throws Exception {
        Mockito.when(service.getAllPayments()).thenReturn(List.of(paymentEntity));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetPaymentById_ReturnsOk() throws Exception {
        Mockito.when(service.getPaymentById(1L)).thenReturn(paymentEntity);

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdatePayment_ReturnsOk() throws Exception {
        paymentEntity.setAmount(2000.0);
        Mockito.when(service.updatePayment(eq(1L), any(PaymentRequest.class))).thenReturn(paymentEntity);

        request.setAmount(2000.0);

        mockMvc.perform(put("/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(2000.0));
    }

    @Test
    void testDeletePayment_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(service, Mockito.times(1)).deletePayment(1L);
    }
}

