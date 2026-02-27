package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.config.TimeProviderConfig;
import com.iprody.payment.service.app.controller.payment.PaymentController;
import com.iprody.payment.service.app.controller.payment.model.PaymentFilterRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.controller.payment.model.PaymentToCreateRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentToPartUpdateRequest;
import com.iprody.payment.service.app.exception.PaymentEntityNotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.iprody.payment.service.app.persistency.entity.PaymentStatus.RECEIVED;
import static com.iprody.payment.service.app.util.CommonConstants.GET;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.STRICT_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureJsonTesters
@MockitoSettings(strictness = STRICT_STUBS)
@Import(TimeProviderConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<PaymentResponse> json;
    @Autowired
    private JacksonTester<List<PaymentResponse>> jsonList;
    @Autowired
    private JacksonTester<Page<PaymentResponse>> jsonPage;
    @Autowired
    private JacksonTester<PaymentToCreateRequest> jsonCreate;
    @Autowired
    private JacksonTester<PaymentToPartUpdateRequest> jsonPartUpdate;

    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private PaymentMapper paymentMapper;

    @Test
    void whenIdExistsThenReturnPayment() throws Exception {
        // given
        final UUID id = UUID.randomUUID();

        final PaymentDto paymentDto = PaymentDto.builder()
                .guid(id)
                .build();

        final PaymentResponse paymentResponse = PaymentResponse.builder()
                .guid(id)
                .build();

        when(paymentService.getById(id)).thenReturn(paymentDto);
        when(paymentMapper.toApiResponse(paymentDto)).thenReturn(paymentResponse);

        // when
        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(json.write(paymentResponse).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).getById(id);
        inOrder.verify(paymentMapper).toApiResponse(paymentDto);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenPaymentDoesNotExistThenReturn404() throws Exception {
        // given
        final UUID id = UUID.randomUUID();

        when(paymentService.getById(id)).thenThrow(new PaymentEntityNotFoundException(id, GET));

        // when
        this.mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).getById(id);
        inOrder.verify(paymentMapper, never()).toApiResponse(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenPaymentsExistThenReturnList() throws Exception {
        // given
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

        // when
        this.mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonList.write(paymentResponses).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).getAllPayments();
        inOrder.verify(paymentMapper).toApiResponse(paymentDto1);
        inOrder.verify(paymentMapper).toApiResponse(paymentDto2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenPaymentsNotExistThenReturnEmptyList() throws Exception {
        // given
        final List<PaymentDto> paymentDtos = Collections.emptyList();

        final List<PaymentResponse> paymentResponses = Collections.emptyList();

        when(paymentService.getAllPayments()).thenReturn(paymentDtos);

        // when
        this.mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonList.write(paymentResponses).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).getAllPayments();
        inOrder.verify(paymentMapper, never()).toApiResponse(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenPaymentsExistThenSearchPagedByFilterReturnsPageOfPaymentResponses() throws Exception {
        // given
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

        // when
        mockMvc.perform(get("/payments/search")
                        .param("status", RECEIVED.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonPage.write(paymentPage).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentMapper).toPaymentFilter(any(PaymentFilterRequest.class));
        inOrder.verify(paymentService).searchPagedByFilter(eq(paymentFilter), any(Pageable.class));
        inOrder.verify(paymentMapper).toApiResponse(paymentDto1);
        inOrder.verify(paymentMapper).toApiResponse(paymentDto2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void cratePaymentTest() throws Exception {
        // given
        final UUID guid = UUID.randomUUID();

        final PaymentToCreateRequest toCreateRequest = PaymentToCreateRequest.builder().build();
        final PaymentDto dtoToCreate = PaymentDto.builder().build();
        final PaymentDto dtoResult = PaymentDto.builder().build();
        final PaymentResponse apiResponse = PaymentResponse.builder()
                .guid(guid)
                .build();

        when(paymentMapper.toDto(toCreateRequest)).thenReturn(dtoToCreate);
        when(paymentService.create(dtoToCreate)).thenReturn(dtoResult);
        when(paymentMapper.toApiResponse(dtoResult)).thenReturn(apiResponse);

        // when
        this.mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreate.write(toCreateRequest).getJson()))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.LOCATION, containsString("/payments/" + guid)))
                .andExpect(content().json(json.write(apiResponse).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentMapper).toDto(toCreateRequest);
        inOrder.verify(paymentService).create(dtoToCreate);
        inOrder.verify(paymentMapper).toApiResponse(dtoResult);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void updatePaymentTest() throws Exception {
        // given
        final UUID guid = UUID.randomUUID();

        final PaymentToCreateRequest toUpdateRequest = PaymentToCreateRequest.builder().build();
        final PaymentDto dtoToUpdate = PaymentDto.builder().build();
        final PaymentDto dtoResult = PaymentDto.builder().build();
        final PaymentResponse apiResponse = PaymentResponse.builder().build();

        when(paymentMapper.toDto(toUpdateRequest)).thenReturn(dtoToUpdate);
        when(paymentService.update(guid, dtoToUpdate)).thenReturn(dtoResult);
        when(paymentMapper.toApiResponse(dtoResult)).thenReturn(apiResponse);

        // when
        this.mockMvc.perform(put("/payments/{id}", guid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreate.write(toUpdateRequest).getJson()))
                .andExpect(status().isOk())
                .andExpect(content().json(json.write(apiResponse).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentMapper).toDto(toUpdateRequest);
        inOrder.verify(paymentService).update(guid, dtoToUpdate);
        inOrder.verify(paymentMapper).toApiResponse(dtoResult);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void updatePartPaymentTest() throws Exception {
        // given
        final UUID guid = UUID.randomUUID();

        final PaymentToPartUpdateRequest toPartUpdateRequest = PaymentToPartUpdateRequest.builder().build();
        final PaymentDto dtoResult = PaymentDto.builder().build();
        final PaymentResponse apiResponse = PaymentResponse.builder().build();

        when(paymentService.updateNote(guid, toPartUpdateRequest)).thenReturn(dtoResult);
        when(paymentMapper.toApiResponse(dtoResult)).thenReturn(apiResponse);

        // when
        this.mockMvc.perform(patch("/payments/{id}/note", guid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPartUpdate.write(toPartUpdateRequest).getJson()))
                .andExpect(status().isOk())
                .andExpect(content().json(json.write(apiResponse).getJson()));

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).updateNote(guid, toPartUpdateRequest);
        inOrder.verify(paymentMapper).toApiResponse(dtoResult);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void deletePaymentTest() throws Exception {
        // given
        final UUID guid = UUID.randomUUID();

        // when
        this.mockMvc.perform(delete("/payments/{id}", guid))
                .andExpect(status().isNoContent());

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentService);
        inOrder.verify(paymentService).deleteById(guid);
        inOrder.verifyNoMoreInteractions();
    }
}
