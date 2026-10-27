package com.activation.api.application.port;

import com.activation.api.domain.model.ActivationRequest;
import com.activation.api.domain.model.ActivationStatus;

import java.util.Optional;
import java.util.UUID;

public interface ActivationRequestRepository {

    ActivationRequest save(ActivationRequest request);

    Optional<ActivationRequest> findById(UUID id);

    PagedResult<ActivationRequest> findAll(ActivationStatus status, String customerId, int page, int size);
}
