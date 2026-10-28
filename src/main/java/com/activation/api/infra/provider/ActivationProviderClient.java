package com.activation.api.infra.provider;

import com.activation.api.application.port.ActivationProvider;
import com.activation.api.domain.model.ActivationRequest;
import com.activation.api.infra.provider.dto.ProviderActivationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

public class ActivationProviderClient implements ActivationProvider {

    private static final Logger log = LoggerFactory.getLogger(ActivationProviderClient.class);

    private final RestClient restClient;

    public ActivationProviderClient(RestClient.Builder restClientBuilder, String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public boolean activate(ActivationRequest request) {
        try {
            restClient.post()
                    .uri("/provider/v1/activate")
                    .header("Idempotency-Key", request.getExternalId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ProviderActivationRequest.from(request))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientResponseException e) {
            log.warn("Provider rejected activation for externalId {} with status {}", request.getExternalId(), e.getStatusCode().value());
            return false;
        } catch (RestClientException e) {
            log.warn("Provider unreachable or timed out for externalId {}", request.getExternalId(), e);
            return false;
        }
    }
}
