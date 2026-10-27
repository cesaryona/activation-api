package com.activation.api.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ActivationRequestJpaRepository extends JpaRepository<ActivationRequestEntity, UUID>,
        JpaSpecificationExecutor<ActivationRequestEntity> {
}
