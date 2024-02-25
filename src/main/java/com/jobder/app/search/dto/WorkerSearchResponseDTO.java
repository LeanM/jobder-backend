package com.jobder.app.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerSearchResponseDTO {
    private List<WorkerSearchDTO> workerSearchData;
    private PaginationResponseDTO paginationData;
}
