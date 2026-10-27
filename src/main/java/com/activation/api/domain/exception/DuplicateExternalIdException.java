package com.activation.api.domain.exception;

public class DuplicateExternalIdException extends DomainException {

    public DuplicateExternalIdException(String externalId) {
        super("Activation request already exists for externalId: " + externalId);
    }
}
