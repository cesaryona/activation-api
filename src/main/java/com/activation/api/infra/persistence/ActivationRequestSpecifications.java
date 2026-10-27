package com.activation.api.infra.persistence;

import com.activation.api.domain.model.ActivationStatus;
import org.springframework.data.jpa.domain.Specification;

final class ActivationRequestSpecifications {

    private ActivationRequestSpecifications() {
    }

    static Specification<ActivationRequestEntity> hasStatus(ActivationStatus status) {
        return status == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    static Specification<ActivationRequestEntity> hasCustomerId(String customerId) {
        return customerId == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.equal(root.get("customerId"), customerId);
    }
}
