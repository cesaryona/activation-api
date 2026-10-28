package com.activation.api.application.port;

import com.activation.api.domain.model.ActivationRequest;

public interface ActivationProvider {

    boolean activate(ActivationRequest request);
}
