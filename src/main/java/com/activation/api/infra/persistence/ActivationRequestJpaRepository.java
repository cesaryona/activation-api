package com.activation.api.infra.persistence;

import com.activation.api.domain.model.ActivationStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ActivationRequestJpaRepository extends JpaRepository<ActivationRequestEntity, UUID>,
        JpaSpecificationExecutor<ActivationRequestEntity> {

    List<ActivationRequestEntity> findByStatusOrderByCreatedAtAsc(ActivationStatus status, Limit limit);
}
