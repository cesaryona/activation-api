package com.activation.api.application.usecase.listrequests;

import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.application.port.PagedResult;
import com.activation.api.domain.model.ActivationRequest;

public class ListActivationRequestsUseCase {

    private final ActivationRequestRepository repository;

    public ListActivationRequestsUseCase(ActivationRequestRepository repository) {
        this.repository = repository;
    }

    public PagedResult<ActivationRequest> execute(ListActivationRequestsInput input) {
        return repository.findAll(input.status(), input.customerId(), input.page(), input.size());
    }
}
