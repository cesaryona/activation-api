package com.activation.api.application.usecase.createrequest;

import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.domain.model.ActivationRequest;

public class CreateActivationRequestUseCase {

    private final ActivationRequestRepository repository;

    public CreateActivationRequestUseCase(ActivationRequestRepository repository) {
        this.repository = repository;
    }

    public ActivationRequest execute(CreateActivationRequestInput input) {
        ActivationRequest request = ActivationRequest.create(
                input.externalId(),
                input.customerId(),
                input.productCode()
        );
        return repository.save(request);
    }
}
