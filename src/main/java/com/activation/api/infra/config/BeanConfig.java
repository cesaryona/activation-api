package com.activation.api.infra.config;

import com.activation.api.application.port.ActivationProvider;
import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.application.usecase.createrequest.CreateActivationRequestUseCase;
import com.activation.api.application.usecase.findrequest.FindActivationRequestUseCase;
import com.activation.api.application.usecase.listrequests.ListActivationRequestsUseCase;
import com.activation.api.application.usecase.processrequests.ProcessPendingActivationRequestsUseCase;
import com.activation.api.infra.provider.ActivationProviderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BeanConfig {

    @Bean
    public CreateActivationRequestUseCase createActivationRequestUseCase(ActivationRequestRepository repository) {
        return new CreateActivationRequestUseCase(repository);
    }

    @Bean
    public FindActivationRequestUseCase findActivationRequestUseCase(ActivationRequestRepository repository) {
        return new FindActivationRequestUseCase(repository);
    }

    @Bean
    public ListActivationRequestsUseCase listActivationRequestsUseCase(ActivationRequestRepository repository) {
        return new ListActivationRequestsUseCase(repository);
    }

    @Bean
    public ActivationProvider activationProvider(RestClient.Builder restClientBuilder,
                                                 @Value("${provider.base-url}") String baseUrl) {
        return new ActivationProviderClient(restClientBuilder, baseUrl);
    }

    @Bean
    public ProcessPendingActivationRequestsUseCase processPendingActivationRequestsUseCase(
            ActivationRequestRepository repository,
            ActivationProvider provider,
            @Value("${scheduler.batch-size}") int batchSize) {
        return new ProcessPendingActivationRequestsUseCase(repository, provider, batchSize);
    }
}
