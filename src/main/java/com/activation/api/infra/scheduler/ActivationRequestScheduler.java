package com.activation.api.infra.scheduler;

import com.activation.api.application.usecase.processrequests.ProcessPendingActivationRequestsUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBooleanProperty(name = "scheduler.enabled", matchIfMissing = true)
public class ActivationRequestScheduler {

    private final ProcessPendingActivationRequestsUseCase processPendingActivationRequestsUseCase;

    public ActivationRequestScheduler(ProcessPendingActivationRequestsUseCase processPendingActivationRequestsUseCase) {
        this.processPendingActivationRequestsUseCase = processPendingActivationRequestsUseCase;
    }

    @Scheduled(fixedDelayString = "${scheduler.fixed-delay}")
    public void processPendingRequests() {
        processPendingActivationRequestsUseCase.execute();
    }
}
