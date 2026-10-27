package com.activation.api.application.usecase.createrequest;

public record CreateActivationRequestInput(
        String externalId,
        String customerId,
        String productCode
) {}
