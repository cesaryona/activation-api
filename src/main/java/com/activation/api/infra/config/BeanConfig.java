package com.activation.api.infra.config;

import com.activation.api.application.port.ActivationRequestRepository;
import com.activation.api.application.usecase.createrequest.CreateActivationRequestUseCase;
import com.activation.api.application.usecase.findrequest.FindActivationRequestUseCase;
import com.activation.api.application.usecase.listrequests.ListActivationRequestsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
