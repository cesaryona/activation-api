package com.activation.api.web.dto;

import com.activation.api.domain.model.ActivationRequest;
import com.activation.api.domain.model.ActivationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ActivationRequestResponse(
        @Schema(example = "0b7f3c1e-5d2a-4c4e-9a51-2f8d7b1e6c90") UUID id,
        @Schema(example = "EXT-12345") String externalId,
        @Schema(example = "CUST-10001") String customerId,
        @Schema(example = "INTERNET-500") String productCode,
        @Schema(example = "PENDING") ActivationStatus status,
        @Schema(description = "UTC, ISO-8601", example = "2026-10-26T14:03:11Z") Instant createdAt,
        @Schema(description = "UTC, ISO-8601", example = "2026-10-26T14:03:11Z") Instant updatedAt) {

    public static ActivationRequestResponse from(ActivationRequest request) {
        return new ActivationRequestResponse(
                request.getId(),
                request.getExternalId(),
                request.getCustomerId(),
                request.getProductCode(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
