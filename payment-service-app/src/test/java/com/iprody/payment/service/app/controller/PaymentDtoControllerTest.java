package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.controller.payment.PaymentController;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
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
class PaymentDtoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void whenIdExistsThenReturnPayment() throws Exception {
        final UUID id = UUID.randomUUID();

        final PaymentDto paymentDto = PaymentDto.builder()
                .guid(id)
                .build();

        when(paymentService.findPaymentById(id)).thenReturn(Optional.of(paymentDto));

        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(paymentDto.guid().toString()));
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
        final PaymentDto paymentDto = PaymentDto.builder()
                .guid(UUID.randomUUID())
                .build();

        final List<PaymentDto> paymentDtos = List.of(paymentDto);

        when(paymentService.getAllPayments()).thenReturn(paymentDtos);

        this.mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].guid").value(paymentDto.guid().toString()));
    }
}