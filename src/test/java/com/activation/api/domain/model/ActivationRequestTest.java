package com.activation.api.domain.model;

import com.activation.api.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class Transitions {

        private final ActivationRequest pending = ActivationRequest.create("ORD-2026-000123", "CUST-48213", "FIBER-500MB");

        @Test
        void completesAfterProcessing() {
            ActivationRequest completed = pending.startProcessing().complete();

            assertThat(completed.getStatus()).isEqualTo(ActivationStatus.COMPLETED);
            assertThat(completed.getId()).isEqualTo(pending.getId());
            assertThat(completed.getCreatedAt()).isEqualTo(pending.getCreatedAt());
            assertThat(completed.getUpdatedAt()).isAfterOrEqualTo(pending.getUpdatedAt());
        }

        @Test
        void failsAfterProcessing() {
            assertThat(pending.startProcessing().fail().getStatus()).isEqualTo(ActivationStatus.FAILED);
        }

        @Test
        void keepsOriginalUnchanged() {
            pending.startProcessing();

            assertThat(pending.getStatus()).isEqualTo(ActivationStatus.PENDING);
        }

        @Test
        void rejectsCompletingWithoutProcessing() {
            assertThatThrownBy(pending::complete)
                    .isInstanceOf(InvalidStatusTransitionException.class)
                    .hasMessage("Activation request " + pending.getId() + " cannot move from PENDING to COMPLETED");
        }

        @Test
        void rejectsLeavingFinalStatus() {
            ActivationRequest completed = pending.startProcessing().complete();
            ActivationRequest failed = pending.startProcessing().fail();

            assertThatThrownBy(completed::startProcessing).isInstanceOf(InvalidStatusTransitionException.class);
            assertThatThrownBy(completed::fail).isInstanceOf(InvalidStatusTransitionException.class);
            assertThatThrownBy(failed::complete).isInstanceOf(InvalidStatusTransitionException.class);
        }

        @Test
        void rejectsProcessingTwice() {
            assertThatThrownBy(pending.startProcessing()::startProcessing)
                    .isInstanceOf(InvalidStatusTransitionException.class);
        }
    }
}
