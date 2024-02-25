package com.jobder.app.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequestDTO {
    private int initialPage;
    private int maxPageItems;
}
