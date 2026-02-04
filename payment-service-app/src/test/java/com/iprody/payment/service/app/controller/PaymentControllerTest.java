package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void whenIdExistsThenReturnPayment() throws Exception {
        final UUID id = UUID.randomUUID();

        final Payment payment = Payment.builder()
                .guid(id)
                .build();

        when(paymentService.findPaymentById(id)).thenReturn(Optional.of(payment));

        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(payment.guid().toString()));
    }

    @Test
    void whenPaymentDoesNotExistThenReturn404() throws Exception {
        final UUID id = UUID.randomUUID();

        when(paymentService.findPaymentById(id)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void whenPaymentsExistThenReturnList() throws Exception {
        final Payment payment = Payment.builder()
                .guid(UUID.randomUUID())
                .build();

        final List<Payment> payments = List.of(payment);

        when(paymentService.getAllPayments()).thenReturn(payments);

        this.mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].guid").value(payment.guid().toString()));
    }
}