package com.activation.api.web.controller;

import com.activation.api.application.usecase.createrequest.CreateActivationRequestUseCase;
import com.activation.api.application.usecase.findrequest.FindActivationRequestUseCase;
import com.activation.api.application.usecase.listrequests.ListActivationRequestsUseCase;
import com.activation.api.domain.model.ActivationRequest;
import com.activation.api.web.dto.ActivationRequestResponse;
import com.activation.api.web.dto.CreateActivationRequestRequest;
import com.activation.api.web.dto.ListActivationRequestsQuery;
import com.activation.api.web.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/requests")
public class ActivationRequestController {

    private final CreateActivationRequestUseCase createUseCase;
    private final FindActivationRequestUseCase findUseCase;
    private final ListActivationRequestsUseCase listUseCase;

    public ActivationRequestController(CreateActivationRequestUseCase createUseCase,
                                       FindActivationRequestUseCase findUseCase,
                                       ListActivationRequestsUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.findUseCase = findUseCase;
        this.listUseCase = listUseCase;
    }

    @Operation(summary = "Create an activation request")
    @ApiResponse(responseCode = "201", description = "Request created as PENDING; Location points to it")
    @ApiResponse(responseCode = "400", description = "Missing or invalid fields",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<ActivationRequestResponse> create(@Valid @RequestBody CreateActivationRequestRequest request) {
        ActivationRequest created = createUseCase.execute(request.toInput());
        return ResponseEntity.created(URI.create("/api/v1/requests/" + created.getId()))
                .body(ActivationRequestResponse.from(created));
    }

    @Operation(summary = "Find an activation request with its current status")
    @ApiResponse(responseCode = "200", description = "Request found")
    @ApiResponse(responseCode = "400", description = "id is not a valid UUID",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Request not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ActivationRequestResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ActivationRequestResponse.from(findUseCase.execute(id)));
    }

    @Operation(summary = "List activation requests, newest first, optionally filtered by status and customer")
    @ApiResponse(responseCode = "200", description = "Page of requests")
    @ApiResponse(responseCode = "400", description = "Invalid status, page or size",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public ResponseEntity<PageResponse<ActivationRequestResponse>> list(@Valid @ParameterObject @ModelAttribute ListActivationRequestsQuery query) {
        return ResponseEntity.ok(PageResponse.from(listUseCase.execute(query.toInput()).map(ActivationRequestResponse::from)));
    }
}
