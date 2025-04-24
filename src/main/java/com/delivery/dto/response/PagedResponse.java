package com.delivery.dto.response;

import com.delivery.model.BaseEntity;
import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public class PagedResponse<T extends BaseEntity> {
    List<EntityApiResponse<T>> content;
    int pageNumber;
    int pageSize;
    int pageNumberOfElements;
    int totalPages;
    long totalElements;

    public PagedResponse(Page<T> page, Function<T, EntityApiResponse<T>> mapper) {
        content = page.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
        pageNumber = page.getNumber();
        pageSize = page.getSize();
        pageNumberOfElements = page.getNumberOfElements();
        totalPages = page.getTotalPages();
        totalElements = page.getTotalElements();
    }
}
