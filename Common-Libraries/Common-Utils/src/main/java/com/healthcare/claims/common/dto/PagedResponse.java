package com.healthcare.claims.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic paginated response wrapper.
 *
 * @param <T> the type of elements in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private ApiStatus status;
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String tenantId;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Creates a {@link PagedResponse} from a Spring Data {@link Page}.
     *
     * @param page the Spring Data page
     * @param <T>  the element type
     * @return a populated {@link PagedResponse}
     */
    public static <T> PagedResponse<T> of(Page<T> page) {
        return PagedResponse.<T>builder()
                .status(ApiStatus.SUCCESS)
                .data(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    /**
     * Creates a {@link PagedResponse} from a Spring Data {@link Page} with a tenant identifier.
     *
     * @param page     the Spring Data page
     * @param tenantId the encrypted tenant identifier
     * @param <T>      the element type
     * @return a populated {@link PagedResponse}
     */
    public static <T> PagedResponse<T> of(Page<T> page, String tenantId) {
        return PagedResponse.<T>builder()
                .status(ApiStatus.SUCCESS)
                .data(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .tenantId(tenantId)
                .build();
    }
}
