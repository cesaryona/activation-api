package com.activation.api.application.usecase.processrequests;

import com.activation.api.application.port.ActivationProvider;
import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.domain.model.ActivationRequest;
import com.activation.api.domain.model.ActivationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessPendingActivationRequestsUseCaseTest {

    private static final int BATCH_SIZE = 20;

    private final ActivationRequestRepository repository = mock(ActivationRequestRepository.class);
    private final ActivationProvider provider = mock(ActivationProvider.class);
    private final ProcessPendingActivationRequestsUseCase useCase =
            new ProcessPendingActivationRequestsUseCase(repository, provider, BATCH_SIZE);

    private final ActivationRequest pending = ActivationRequest.create("ORD-2026-000123", "CUST-48213", "FIBER-500MB");

    @BeforeEach
    void saveReturnsWhatItReceives() {
        when(repository.save(any())).thenAnswer(returnsFirstArg());
    }

    @Test
    void completesWhenProviderAccepts() {
        when(repository.findPending(BATCH_SIZE)).thenReturn(List.of(pending));
        when(provider.activate(any())).thenReturn(true);

        useCase.execute();

        assertThat(savedStatuses()).containsExactly(ActivationStatus.PROCESSING, ActivationStatus.COMPLETED);
    }

    @Test
    void failsWhenProviderRejects() {
        when(repository.findPending(BATCH_SIZE)).thenReturn(List.of(pending));
        when(provider.activate(any())).thenReturn(false);

        useCase.execute();

        assertThat(savedStatuses()).containsExactly(ActivationStatus.PROCESSING, ActivationStatus.FAILED);
    }

    @Test
    void savesProcessingBeforeCallingProvider() {
        when(repository.findPending(BATCH_SIZE)).thenReturn(List.of(pending));
        when(provider.activate(any())).thenReturn(true);

        useCase.execute();

        InOrder order = inOrder(repository, provider);
        order.verify(repository).save(any());
        order.verify(provider).activate(any());
        order.verify(repository).save(any());
    }

    @Test
    void keepsProcessingOthersWhenOneFails() {
        ActivationRequest another = ActivationRequest.create("ORD-2026-000124", "CUST-48213", "TV-PREMIUM");
        when(repository.findPending(BATCH_SIZE)).thenReturn(List.of(pending, another));
        when(provider.activate(any()))
                .thenThrow(new IllegalStateException("unexpected"))
                .thenReturn(true);

        useCase.execute();

        verify(provider, times(2)).activate(any());
        assertThat(savedStatuses()).endsWith(ActivationStatus.COMPLETED);
    }

    private List<ActivationStatus> savedStatuses() {
        ArgumentCaptor<ActivationRequest> saved = ArgumentCaptor.forClass(ActivationRequest.class);
        verify(repository, atLeastOnce()).save(saved.capture());
        return saved.getAllValues().stream().map(ActivationRequest::getStatus).toList();
    }
}
