package com.activation.api.application.usecase.processrequests;

import com.activation.api.application.port.ActivationProvider;
import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.domain.model.ActivationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessPendingActivationRequestsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingActivationRequestsUseCase.class);

    private final int batchSize;
    private final ActivationProvider provider;
    private final ActivationRequestRepository repository;

    public ProcessPendingActivationRequestsUseCase(ActivationRequestRepository repository,
                                                   ActivationProvider provider,
                                                   int batchSize) {
        this.repository = repository;
        this.provider = provider;
        this.batchSize = batchSize;
    }

    public void execute() {
        for (ActivationRequest pending : repository.findPending(batchSize)) {
            try {
                process(pending);
            } catch (RuntimeException e) {
                log.error("Failed to process activation request {}", pending.getId(), e);
            }
        }
    }

    private void process(ActivationRequest pending) {
        ActivationRequest processing = repository.save(pending.startProcessing());
        ActivationRequest result = provider.activate(processing) ? processing.complete() : processing.fail();
        repository.save(result);
    }
}
