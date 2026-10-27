package com.activation.api.application.port;

import java.util.List;
import java.util.function.Function;

public record PagedResult<T>(List<T> items, int page, int size, long totalItems, int totalPages) {

    public <R> PagedResult<R> map(Function<T, R> mapper) {
        return new PagedResult<>(items.stream().map(mapper).toList(), page, size, totalItems, totalPages);
    }
}
