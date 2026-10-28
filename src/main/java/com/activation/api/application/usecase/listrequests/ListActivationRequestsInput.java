package com.activation.api.application.usecase.listrequests;

import com.activation.api.domain.model.ActivationStatus;

public record ListActivationRequestsInput(
        ActivationStatus status,
        String customerId,
        int page,
        int size
) {
}
