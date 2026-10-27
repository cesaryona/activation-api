package com.activation.api.web.controller;

import com.activation.api.domain.model.ActivationStatus;
import com.activation.api.infra.persistence.ActivationRequestEntity;
import com.activation.api.infra.persistence.ActivationRequestJpaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActivationRequestControllerTest {

    private static final String UTC_ISO_8601 = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivationRequestJpaRepository jpaRepository;

    @BeforeEach
    void cleanDatabase() {
        jpaRepository.deleteAll();
    }

    @Nested
    class Create {

        @Test
        void createsRequestAsPending() throws Exception {
            String body = create("valid.json")
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.externalId").value("ORD-2026-000123"))
                    .andExpect(jsonPath("$.customerId").value("CUST-48213"))
                    .andExpect(jsonPath("$.productCode").value("FIBER-500MB"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.createdAt", matchesPattern(UTC_ISO_8601)))
                    .andExpect(jsonPath("$.updatedAt", matchesPattern(UTC_ISO_8601)))
                    .andReturn().getResponse().getContentAsString();

            String id = JsonPath.read(body, "$.id");
            assertThat(jpaRepository.findById(UUID.fromString(id))).isPresent();
        }

        @Test
        void returnsLocationOfCreatedRequest() throws Exception {
            MockHttpServletResponse response = create("valid.json")
                    .andExpect(status().isCreated())
                    .andReturn().getResponse();

            String id = JsonPath.read(response.getContentAsString(), "$.id");
            assertThat(response.getHeader("Location")).isEqualTo("/api/v1/requests/" + id);
        }

        @Test
        void rejectsDuplicateExternalId() throws Exception {
            create("valid.json").andExpect(status().isCreated());

            create("same-external-id-different-content.json")
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("Activation request already exists for externalId: ORD-2026-000123"));

            assertThat(jpaRepository.count()).isEqualTo(1);
        }

        @Test
        void rejectsMissingRequiredFields() throws Exception {
            create("blank-external-id-and-missing-product-code.json")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("Validation failed"))
                    .andExpect(jsonPath("$.errors", hasItem(startsWith("externalId: "))))
                    .andExpect(jsonPath("$.errors", hasItem(startsWith("productCode: "))));

            assertThat(jpaRepository.count()).isZero();
        }

        @Test
        void rejectsInvalidFormatAndLength() throws Exception {
            create("external-id-with-space-and-product-code-too-long.json")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasItem("externalId: must contain only letters, digits, '-' or '_'")))
                    .andExpect(jsonPath("$.errors", hasItem(startsWith("productCode: size must be"))));

            assertThat(jpaRepository.count()).isZero();
        }

        @Test
        void rejectsMalformedBody() throws Exception {
            create("malformed.json")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("Malformed request body"));
        }
    }

    @Nested
    class FindById {

        @Test
        void findsExistingRequestWithCurrentStatus() throws Exception {
            String body = create("valid.json")
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
            String id = JsonPath.read(body, "$.id");
            String createdAt = JsonPath.read(body, "$.createdAt");

            mockMvc.perform(get("/api/v1/requests/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.externalId").value("ORD-2026-000123"))
                    .andExpect(jsonPath("$.customerId").value("CUST-48213"))
                    .andExpect(jsonPath("$.productCode").value("FIBER-500MB"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.createdAt").value(createdAt));
        }

        @Test
        void returnsNotFoundForUnknownId() throws Exception {
            UUID unknownId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/requests/{id}", unknownId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("Activation request not found: " + unknownId));
        }

        @Test
        void rejectsInvalidId() throws Exception {
            mockMvc.perform(get("/api/v1/requests/{id}", "not-a-uuid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("Invalid value for parameter: id"));
        }
    }

    @Nested
    class ListRequests {

        @Test
        void listsNewestFirstWithPagination() throws Exception {
            persist("ORD-2026-000101", "CUST-48213", ActivationStatus.PENDING, "2026-10-26T10:00:00Z");
            persist("ORD-2026-000102", "CUST-48213", ActivationStatus.PENDING, "2026-10-26T11:00:00Z");
            persist("ORD-2026-000103", "CUST-48213", ActivationStatus.PENDING, "2026-10-26T12:00:00Z");

            mockMvc.perform(get("/api/v1/requests").param("page", "0").param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[*].externalId", contains("ORD-2026-000103", "ORD-2026-000102")))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.totalElements").value(3))
                    .andExpect(jsonPath("$.totalPages").value(2));

            mockMvc.perform(get("/api/v1/requests").param("page", "1").param("size", "2"))
                    .andExpect(jsonPath("$.content[*].externalId", contains("ORD-2026-000101")));
        }

        @Test
        void filtersByStatusAndCustomer() throws Exception {
            persist("ORD-2026-000101", "CUST-48213", ActivationStatus.PENDING, "2026-10-26T10:00:00Z");
            persist("ORD-2026-000102", "CUST-48213", ActivationStatus.COMPLETED, "2026-10-26T11:00:00Z");
            persist("ORD-2026-000103", "CUST-77102", ActivationStatus.COMPLETED, "2026-10-26T12:00:00Z");

            mockMvc.perform(get("/api/v1/requests").param("status", "COMPLETED"))
                    .andExpect(jsonPath("$.content[*].externalId", contains("ORD-2026-000103", "ORD-2026-000102")));
            mockMvc.perform(get("/api/v1/requests").param("customerId", "CUST-48213"))
                    .andExpect(jsonPath("$.content[*].externalId", contains("ORD-2026-000102", "ORD-2026-000101")));
            mockMvc.perform(get("/api/v1/requests").param("status", "COMPLETED").param("customerId", "CUST-48213"))
                    .andExpect(jsonPath("$.content[*].externalId", contains("ORD-2026-000102")));
        }

        @Test
        void rejectsInvalidListParameters() throws Exception {
            mockMvc.perform(get("/api/v1/requests").param("size", "101").param("page", "-1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", contains(startsWith("page: "), startsWith("size: "))));
            mockMvc.perform(get("/api/v1/requests").param("status", "UNKNOWN"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", contains("status: invalid value")));
        }
    }

    private ResultActions create(String requestFile) throws Exception {
        return mockMvc.perform(post("/api/v1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(requestFile)));
    }

    private static String json(String requestFile) throws IOException {
        return new ClassPathResource("json/create-activation-request/" + requestFile).getContentAsString(StandardCharsets.UTF_8);
    }

    private void persist(String externalId, String customerId, ActivationStatus status, String createdAt) {
        ActivationRequestEntity entity = new ActivationRequestEntity();
        entity.setId(UUID.randomUUID());
        entity.setExternalId(externalId);
        entity.setCustomerId(customerId);
        entity.setProductCode("FIBER-500MB");
        entity.setStatus(status);
        entity.setCreatedAt(Instant.parse(createdAt));
        entity.setUpdatedAt(Instant.parse(createdAt));
        jpaRepository.save(entity);
    }
}
