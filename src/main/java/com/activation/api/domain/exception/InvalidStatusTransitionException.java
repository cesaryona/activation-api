package com.activation.api.domain.exception;

import com.activation.api.domain.model.ActivationStatus;

import java.util.UUID;

public class InvalidStatusTransitionException extends DomainException {

    public InvalidStatusTransitionException(UUID id, ActivationStatus from, ActivationStatus to) {
        super("Activation request " + id + " cannot move from " + from + " to " + to);
    }
}
