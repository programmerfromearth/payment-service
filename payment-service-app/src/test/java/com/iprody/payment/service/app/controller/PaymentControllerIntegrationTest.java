package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.AbstractPostgresIntegrationTest;
import com.iprody.payment.service.app.TestJwtFactory;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.controller.payment.model.PaymentToCreateRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentToPartUpdateRequest;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldReturnOnlyLiquibasePaymentsWhenRetrievingAllPayments() throws Exception {
        mockMvc.perform(get("/payments")
                        .with(getAdmin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void unauthorizedWhenCallRetrievingAllPaymentsWithoutAnyToken() throws Exception {
        mockMvc.perform(get("/payments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnOnlyLiquibasePaymentsWhenRetrievingAllPaymentsWithSearchParams() throws Exception {
        mockMvc.perform(get("/payments/search")
                        .with(getAdmin())
                        .param("page", "0")
                        .param("size", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void shouldReturnPaymentByExistingId() throws Exception {
        final UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        mockMvc.perform(get("/payments/" + existingId)
                        .with(getAdmin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(existingId.toString()))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.amount").value(99.99));

    }

    @Test
    void shouldReturnNotFoundWhenRetrievingPaymentByNotExistingId() throws Exception {
        final UUID notExistingId = UUID.randomUUID();

        mockMvc.perform(get("/payments/" + notExistingId)
                        .with(getAdmin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        final UUID inquiryRefId = UUID.randomUUID();
        final UUID transactionRefId = UUID.randomUUID();
        final double amount = 123.45;
        final String currency = "EUR";
        final String note = "note";

        final PaymentToCreateRequest toCreateRequest = PaymentToCreateRequest.builder()
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal(Double.toString(amount)))
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .transactionRefId(transactionRefId)
                .note(note)
                .build();

        final String toCreateJson = objectMapper.writeValueAsString(toCreateRequest);

        final String response = mockMvc.perform(post("/payments")
                        .with(getAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toCreateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.inquiryRefId").value(inquiryRefId.toString()))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.currency").value(currency))
                .andExpect(jsonPath("$.status").value(PaymentStatus.PENDING.toString()))
                .andExpect(jsonPath("$.transactionRefId").value(transactionRefId.toString()))
                .andExpect(jsonPath("$.note").value(note))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final PaymentResponse created = objectMapper.readValue(response, PaymentResponse.class);
        final Optional<PaymentEntity> saved = paymentRepository.findById(created.guid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo(currency);
        assertThat(saved.get().getAmount()).isEqualByComparingTo(Double.toString(amount));
    }

    @Test
    void shouldPartialUpdatePaymentAndVerifyInDatabase() throws Exception {
        final UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final String updatedNote = "updated note";

        final PaymentToPartUpdateRequest toPartUpdateRequest = PaymentToPartUpdateRequest.builder()
                .note(updatedNote)
                .build();
        final String toPartUpdateJson = objectMapper.writeValueAsString(toPartUpdateRequest);

        final Optional<PaymentEntity> beforeUpdate = paymentRepository.findById(existingId);
        assertThat(beforeUpdate).isPresent();

        final String response = mockMvc.perform(patch("/payments/%s/note".formatted(existingId.toString()))
                        .with(getAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPartUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(existingId.toString()))
                .andExpect(jsonPath("$.note").value(updatedNote))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final PaymentResponse created = objectMapper.readValue(response, PaymentResponse.class);
        final Optional<PaymentEntity> saved = paymentRepository.findById(created.guid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getNote()).isEqualTo(updatedNote);
        assertThat(beforeUpdate.get().getNote()).isNotEqualTo(updatedNote);
    }

    @Test
    void shouldDeletePaymentAndVerifyInDatabase() throws Exception {
        final UUID existingId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        final boolean existBeforeDeleting = paymentRepository.existsById(existingId);
        assertTrue(existBeforeDeleting);

        mockMvc.perform(delete("/payments/%s".formatted(existingId.toString()))
                        .with(getAdmin())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        final boolean existAfterDeleting = paymentRepository.existsById(existingId);
        assertFalse(existAfterDeleting);
    }

    private static RequestPostProcessor getAdmin() {
        return TestJwtFactory.jwtWithRole("test-user", "admin");
    }
}
