package com.jobder.app.search.controllers;

import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.models.User;
import com.jobder.app.search.dto.RequestClientSearchInfo;
import com.jobder.app.search.dto.WorkerSearchResponse;
import com.jobder.app.search.services.WorkerSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping ( path = "search" )
@RequiredArgsConstructor
public class WorkerSearchController {

    private final WorkerSearchService workerSearchService;

    @PostMapping (path = "/client/searchWorkers")
    public ResponseEntity<?> searchWorkers(@RequestBody RequestClientSearchInfo clientSearchInfo, @AuthenticationPrincipal User clientSearching){
        ResponseEntity<?> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        try {
            clientSearchInfo.setId(clientSearching.getId());
            List<WorkerSearchResponse> toReturn = workerSearchService.searchWorkers(clientSearchInfo);
            response = new ResponseEntity<>(toReturn,headers,HttpStatus.OK);
        }
        catch(InvalidClientException e){
            response = new ResponseEntity<String>(e.getMessage(), headers, 404);
        }

        return response;
    }

    @PostMapping (path = "/unlogged/searchWorkers")
    public ResponseEntity<List<WorkerSearchResponse>> searchWorkersUnlogged(@RequestBody RequestClientSearchInfo unloggedSearchInfo){
        ResponseEntity<List<WorkerSearchResponse>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<WorkerSearchResponse> toReturn = workerSearchService.unloggedSearchWorkers(unloggedSearchInfo);

        response = new ResponseEntity<>(toReturn,headers,HttpStatus.OK);

        return response;
    }

}
