package com.activation.api.web.dto;

import com.activation.api.application.usecase.createrequest.CreateActivationRequestInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateActivationRequestRequest(
        @Schema(description = "Unique identifier from the calling system, used to detect duplicates", example = "EXT-12345")
        @NotBlank @Size(max = 100) @Pattern(regexp = CODE_PATTERN, message = CODE_MESSAGE)
        String externalId,

        @Schema(description = "Customer identifier", example = "CUST-10001")
        @NotBlank @Size(max = 100) @Pattern(regexp = CODE_PATTERN, message = CODE_MESSAGE)
        String customerId,

        @Schema(description = "Product to activate", example = "INTERNET-500")
        @NotBlank @Size(max = 50) @Pattern(regexp = CODE_PATTERN, message = CODE_MESSAGE)
        String productCode) {

    static final String CODE_PATTERN = "^[A-Za-z0-9_-]+$";
    static final String CODE_MESSAGE = "must contain only letters, digits, '-' or '_'";

    public CreateActivationRequestInput toInput() {
        return new CreateActivationRequestInput(externalId, customerId, productCode);
    }
}
