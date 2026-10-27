package com.activation.api.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class ActivationRequest {

    private final UUID id;
    private final String externalId;
    private final String customerId;
    private final String productCode;
    private final ActivationStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    private ActivationRequest(UUID id,
                              String externalId,
                              String customerId,
                              String productCode,
                              ActivationStatus status,
                              Instant createdAt,
                              Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.customerId = customerId;
        this.productCode = productCode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ActivationRequest create(String externalId, String customerId, String productCode) {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        return new ActivationRequest(
                UUID.randomUUID(),
                Objects.requireNonNull(externalId, "externalId"),
                Objects.requireNonNull(customerId, "customerId"),
                Objects.requireNonNull(productCode, "productCode"),
                ActivationStatus.PENDING,
                now,
                now
        );
    }

    public static ActivationRequest restore(UUID id,
                                            String externalId,
                                            String customerId,
                                            String productCode,
                                            ActivationStatus status,
                                            Instant createdAt,
                                            Instant updatedAt) {
        return new ActivationRequest(
                Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(externalId, "externalId"),
                Objects.requireNonNull(customerId, "customerId"),
                Objects.requireNonNull(productCode, "productCode"),
                Objects.requireNonNull(status, "status"),
                Objects.requireNonNull(createdAt, "createdAt"),
                Objects.requireNonNull(updatedAt, "updatedAt")
        );
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductCode() {
        return productCode;
    }

    public ActivationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
