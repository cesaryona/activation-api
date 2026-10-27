package com.activation.api.infra.persistence;

import com.activation.api.domain.model.ActivationRequest;

final class ActivationRequestEntityMapper {

    private ActivationRequestEntityMapper() {
    }

    static ActivationRequestEntity toEntity(ActivationRequest request) {
        ActivationRequestEntity entity = new ActivationRequestEntity();
        entity.setId(request.getId());
        entity.setExternalId(request.getExternalId());
        entity.setCustomerId(request.getCustomerId());
        entity.setProductCode(request.getProductCode());
        entity.setStatus(request.getStatus());
        entity.setCreatedAt(request.getCreatedAt());
        entity.setUpdatedAt(request.getUpdatedAt());

        return entity;
    }

    static ActivationRequest toDomain(ActivationRequestEntity entity) {
        return ActivationRequest.restore(
                entity.getId(),
                entity.getExternalId(),
                entity.getCustomerId(),
                entity.getProductCode(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
