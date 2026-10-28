package com.activation.api.domain.model;

public enum ActivationStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED;

    public boolean canTransitionTo(ActivationStatus next) {
        return switch (this) {
            case PENDING -> next == PROCESSING;
            case PROCESSING -> next == COMPLETED || next == FAILED;
            case COMPLETED, FAILED -> false;
        };
    }
}
