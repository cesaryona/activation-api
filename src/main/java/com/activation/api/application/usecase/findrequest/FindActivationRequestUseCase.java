package com.activation.api.application.usecase.findrequest;

import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.domain.exception.ActivationRequestNotFoundException;
import com.activation.api.domain.model.ActivationRequest;

import java.util.UUID;

public class FindActivationRequestUseCase {

    private final ActivationRequestRepository repository;

    public FindActivationRequestUseCase(ActivationRequestRepository repository) {
        this.repository = repository;
    }

    public ActivationRequest execute(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ActivationRequestNotFoundException(id));
    }
}
