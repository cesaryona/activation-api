package com.activation.api.web.dto;

import com.activation.api.application.port.PagedResult;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PageResponse<T> from(PagedResult<T> result) {
        return new PageResponse<>(result.items(), result.page(), result.size(), result.totalItems(), result.totalPages());
    }
}
