package com.activation.api.web.dto;

import com.activation.api.application.usecase.listrequests.ListActivationRequestsInput;
import com.activation.api.domain.model.ActivationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ListActivationRequestsQuery(
        ActivationStatus status,
        String customerId,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size) {

    public ListActivationRequestsQuery {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }

    public ListActivationRequestsInput toInput() {
        return new ListActivationRequestsInput(status, customerId, page, size);
    }
}
