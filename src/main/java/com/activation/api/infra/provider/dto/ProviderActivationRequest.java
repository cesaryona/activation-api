package com.activation.api.infra.provider.dto;

import com.activation.api.domain.model.ActivationRequest;

public record ProviderActivationRequest(String externalId,
                                        String customerId,
                                        String productCode) {

    public static ProviderActivationRequest from(ActivationRequest request) {
        return new ProviderActivationRequest(request.getExternalId(), request.getCustomerId(), request.getProductCode());
    }
}
