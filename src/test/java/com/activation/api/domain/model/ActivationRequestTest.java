package com.activation.api.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivationRequestTest {

    @Test
    void createsAsPending() {
        ActivationRequest request = ActivationRequest.create("ext-1", "customer-1", "PRODUCT-A");

        assertThat(request.getId()).isNotNull();
        assertThat(request.getStatus()).isEqualTo(ActivationStatus.PENDING);
        assertThat(request.getCreatedAt()).isEqualTo(request.getUpdatedAt());
    }

    @Test
    void rejectsMissingExternalId() {
        assertThatThrownBy(() -> ActivationRequest.create(null, "customer-1", "PRODUCT-A"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("externalId");
    }
}
