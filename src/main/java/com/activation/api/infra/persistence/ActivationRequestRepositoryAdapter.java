package com.activation.api.infra.persistence;

import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.application.port.PagedResult;
import com.activation.api.domain.exception.DuplicateExternalIdException;
import com.activation.api.domain.model.ActivationRequest;
import com.activation.api.domain.model.ActivationStatus;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.activation.api.infra.persistence.ActivationRequestSpecifications.hasCustomerId;
import static com.activation.api.infra.persistence.ActivationRequestSpecifications.hasStatus;

@Component
public class ActivationRequestRepositoryAdapter implements ActivationRequestRepository {

    private static final String EXTERNAL_ID_CONSTRAINT = "uk_activation_request_external_id";

    private final ActivationRequestJpaRepository jpaRepository;

    public ActivationRequestRepositoryAdapter(ActivationRequestJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ActivationRequest save(ActivationRequest request) {
        try {
            jpaRepository.saveAndFlush(ActivationRequestEntityMapper.toEntity(request));
        } catch (DataIntegrityViolationException e) {
            if (isExternalIdViolation(e)) {
                throw new DuplicateExternalIdException(request.getExternalId());
            }
            throw e;
        }
        return request;
    }

    private static boolean isExternalIdViolation(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException cve
                && EXTERNAL_ID_CONSTRAINT.equalsIgnoreCase(cve.getConstraintName());
    }

    @Override
    public Optional<ActivationRequest> findById(UUID id) {
        return jpaRepository.findById(id).map(ActivationRequestEntityMapper::toDomain);
    }

    @Override
    public PagedResult<ActivationRequest> findAll(ActivationStatus status, String customerId, int page, int size) {
        Specification<ActivationRequestEntity> spec = hasStatus(status).and(hasCustomerId(customerId));
        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

        Page<ActivationRequestEntity> result = jpaRepository.findAll(spec, PageRequest.of(page, size, sort));
        return new PagedResult<>(
                result.map(ActivationRequestEntityMapper::toDomain).getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public List<ActivationRequest> findPending(int limit) {
        return jpaRepository.findByStatusOrderByCreatedAtAsc(ActivationStatus.PENDING, Limit.of(limit)).stream()
                .map(ActivationRequestEntityMapper::toDomain)
                .toList();
    }
}
