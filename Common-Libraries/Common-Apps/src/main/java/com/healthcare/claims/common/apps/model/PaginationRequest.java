package com.healthcare.claims.common.apps.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    private int page = 0;
    private int size = 20;
    private String sortBy;
    private String sortDirection;
}
