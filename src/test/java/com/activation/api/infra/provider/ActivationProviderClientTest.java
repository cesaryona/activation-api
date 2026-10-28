package com.activation.api.infra.provider;

import com.activation.api.domain.model.ActivationRequest;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class ActivationProviderClientTest {

    private static final String ACTIVATE_PATH = "/provider/v1/activate";

    @RegisterExtension
    static WireMockExtension provider = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private final ActivationRequest request = ActivationRequest.create("ORD-2026-000123", "CUST-48213", "FIBER-500MB");

    private ActivationProviderClient client() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofMillis(300));
        return new ActivationProviderClient(RestClient.builder().requestFactory(requestFactory), provider.baseUrl());
    }

    @Test
    void returnsSuccessWhenProviderAccepts() throws IOException {
        provider.stubFor(post(ACTIVATE_PATH).willReturn(aResponse().withStatus(200)));

        assertThat(client().activate(request)).isTrue();

        provider.verify(postRequestedFor(urlEqualTo(ACTIVATE_PATH))
                .withHeader("Idempotency-Key", equalTo("ORD-2026-000123"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(json("activate-request.json"))));
    }

    @Test
    void returnsFailureWhenProviderRejects() {
        provider.stubFor(post(ACTIVATE_PATH).willReturn(aResponse().withStatus(400)));

        assertThat(client().activate(request)).isFalse();
    }

    @Test
    void returnsFailureWhenProviderErrors() {
        provider.stubFor(post(ACTIVATE_PATH).willReturn(aResponse().withStatus(500)));

        assertThat(client().activate(request)).isFalse();
    }

    @Test
    void returnsFailureWhenProviderTimesOut() {
        provider.stubFor(post(ACTIVATE_PATH).willReturn(aResponse().withStatus(200).withFixedDelay(1_000)));

        assertThat(client().activate(request)).isFalse();
    }

    private static String json(String file) throws IOException {
        return new ClassPathResource("json/provider/" + file).getContentAsString(StandardCharsets.UTF_8);
    }
}
