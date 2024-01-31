package com.jobder.app.search.controllers;

import com.jobder.app.authentication.models.User;
import com.jobder.app.search.dto.RequestClientSearchInfo;
import com.jobder.app.search.services.WorkerSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping ( path = "search" )
@RequiredArgsConstructor
public class WorkerSearchController {

    private final WorkerSearchService workerSearchService;

    @PostMapping (path = "/client/searchWorkers")
    public ResponseEntity<List<User>> searchWorkers(@RequestBody RequestClientSearchInfo clientSearchInfo, @AuthenticationPrincipal User clientSearching){
        ResponseEntity<List<User>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        clientSearchInfo.setId(clientSearching.getId());
        List<User> toReturn = workerSearchService.searchWorkers(clientSearchInfo);

        response = new ResponseEntity<>(toReturn,headers,HttpStatus.OK);

        return response;
    }

    @PostMapping (path = "/unlogged/searchWorkers")
    public ResponseEntity<List<User>> searchWorkersUnlogged(@RequestBody RequestClientSearchInfo unloggedSearchInfo){
        ResponseEntity<List<User>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<User> toReturn = workerSearchService.unloggedSearchWorkers(unloggedSearchInfo);

        response = new ResponseEntity<>(toReturn,headers,HttpStatus.OK);

        return response;
    }

}
