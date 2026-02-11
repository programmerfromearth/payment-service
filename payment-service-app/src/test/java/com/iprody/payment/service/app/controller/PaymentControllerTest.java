package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.controller.payment.PaymentController;
import com.iprody.payment.service.app.controller.payment.model.PaymentFilterRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.iprody.payment.service.app.persistency.entity.PaymentStatus.RECEIVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureJsonTesters
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<PaymentResponse> json;
    @Autowired
    private JacksonTester<List<PaymentResponse>> jsonList;
    @Autowired
    private JacksonTester<Page<PaymentResponse>> jsonPage;

    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private PaymentMapper paymentMapper;

    @Test
    void whenIdExistsThenReturnPayment() throws Exception {
        final UUID id = UUID.randomUUID();

        final PaymentDto paymentDto = PaymentDto.builder()
                .guid(id)
                .build();

        final PaymentResponse paymentResponse = PaymentResponse.builder()
                .guid(id)
                .build();

        when(paymentService.findPaymentById(id)).thenReturn(Optional.of(paymentDto));
        when(paymentMapper.toApiResponse(paymentDto)).thenReturn(paymentResponse);

        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(json.write(paymentResponse).getJson()));

        InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).findPaymentById(id);
        inOrder.verify(paymentMapper).toApiResponse(paymentDto);
    }

    @Test
    void whenPaymentDoesNotExistThenReturn404() throws Exception {
        final UUID id = UUID.randomUUID();

        when(paymentService.findPaymentById(id)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).findPaymentById(id);
        inOrder.verify(paymentMapper, never()).toApiResponse(any());
    }

    @Test
    void whenPaymentsExistThenReturnList() throws Exception {
        final UUID guid1 = UUID.randomUUID();
        final UUID guid2 = UUID.randomUUID();

        final PaymentDto paymentDto1 = PaymentDto.builder()
                .guid(guid1)
                .build();
        final PaymentDto paymentDto2 = PaymentDto.builder()
                .guid(guid2)
                .build();
        final List<PaymentDto> paymentDtos = List.of(paymentDto1, paymentDto2);

        final PaymentResponse paymentResponse1 = PaymentResponse.builder()
                .guid(guid1)
                .build();
        final PaymentResponse paymentResponse2 = PaymentResponse.builder()
                .guid(guid2)
                .build();
        final List<PaymentResponse> paymentResponses = List.of(paymentResponse1, paymentResponse2);

        when(paymentService.getAllPayments()).thenReturn(paymentDtos);
        when(paymentMapper.toApiResponse(paymentDto1)).thenReturn(paymentResponse1);
        when(paymentMapper.toApiResponse(paymentDto2)).thenReturn(paymentResponse2);

        this.mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonList.write(paymentResponses).getJson()));

        InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).getAllPayments();
        inOrder.verify(paymentMapper).toApiResponse(paymentDto1);
        inOrder.verify(paymentMapper).toApiResponse(paymentDto2);
    }

    @Test
    void whenPaymentsNotExistThenReturnEmptyList() throws Exception {
        final List<PaymentDto> paymentDtos = Collections.emptyList();

        final List<PaymentResponse> paymentResponses = Collections.emptyList();

        when(paymentService.getAllPayments()).thenReturn(paymentDtos);

        this.mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonList.write(paymentResponses).getJson()));

        InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).getAllPayments();
        inOrder.verify(paymentMapper, never()).toApiResponse(any());
    }

    @Test
    void whenPaymentsExistThenSearchPagedByFilterReturnsPageOfPaymentResponses() throws Exception {
        final int pageNumber = 0;
        final int pageSize = 25;
        final int total = 2;

        final UUID guid1 = UUID.randomUUID();
        final UUID guid2 = UUID.randomUUID();

        final PaymentDto paymentDto1 = PaymentDto.builder()
                .guid(guid1)
                .build();
        final PaymentDto paymentDto2 = PaymentDto.builder()
                .guid(guid2)
                .build();
        final List<PaymentDto> paymentDtos = List.of(paymentDto1, paymentDto2);

        final Page<PaymentDto> paymentDtoPage = new PageImpl<>(
                paymentDtos,
                PageRequest.of(pageNumber, pageSize),
                total);

        final PaymentResponse paymentResponse1 = PaymentResponse.builder()
                .guid(guid1)
                .build();
        final PaymentResponse paymentResponse2 = PaymentResponse.builder()
                .guid(guid2)
                .build();
        final List<PaymentResponse> paymentResponses = List.of(paymentResponse1, paymentResponse2);

        final Page<PaymentResponse> paymentPage = new PageImpl<>(
                paymentResponses,
                PageRequest.of(pageNumber, pageSize),
                total);

        final PaymentFilterRequest filterRequest = PaymentFilterRequest.builder()
                .status(RECEIVED)
                .build();

        final PaymentFilter paymentFilter = PaymentFilter.builder()
                .build();

        when(paymentMapper.toPaymentFilter(filterRequest)).thenReturn(paymentFilter);
        when(paymentService.searchPagedByFilter(eq(paymentFilter), any(Pageable.class))).thenReturn(paymentDtoPage);
        when(paymentMapper.toApiResponse(paymentDto1)).thenReturn(paymentResponse1);
        when(paymentMapper.toApiResponse(paymentDto2)).thenReturn(paymentResponse2);

        mockMvc.perform(get("/payments/search")
                        .param("status", RECEIVED.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonPage.write(paymentPage).getJson()));

        InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentMapper).toPaymentFilter(any(PaymentFilterRequest.class));
        inOrder.verify(paymentService).searchPagedByFilter(eq(paymentFilter), any(Pageable.class));
        inOrder.verify(paymentMapper).toApiResponse(paymentDto1);
        inOrder.verify(paymentMapper).toApiResponse(paymentDto2);
    }
}