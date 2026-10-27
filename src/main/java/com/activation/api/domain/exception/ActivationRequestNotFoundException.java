package com.activation.api.domain.exception;

import java.util.UUID;

public class ActivationRequestNotFoundException extends DomainException {

    public ActivationRequestNotFoundException(UUID id) {
        super("Activation request not found: " + id);
    }
}
