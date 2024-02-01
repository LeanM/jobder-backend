package com.jobder.app.matching.controllers;

import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.exceptions.InvalidWorkerException;
import com.jobder.app.authentication.models.User;
import com.jobder.app.matching.dto.ClientMatchesReponseDTO;
import com.jobder.app.matching.dto.InteractionRequest;
import com.jobder.app.matching.dto.MatchRequest;
import com.jobder.app.matching.exceptions.InvalidInteractionException;
import com.jobder.app.matching.services.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping( path = "matching" )
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping(path = "/client/interaction")
    public ResponseEntity<String> interactWithWorker(@RequestBody InteractionRequest interactionRequest, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            interactionRequest.setClientId(user.getId());
            matchingService.interactWithWorker(interactionRequest);
            response = new ResponseEntity<>("Interaction completed!", headers, HttpStatus.OK);
        }
        catch(InvalidInteractionException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping(path = "/worker/match")
    public ResponseEntity<String> matchWithClient(@RequestBody MatchRequest matchRequest, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            matchRequest.setWorkerId(user.getId());
            matchingService.matchWithClient(matchRequest);
            response = new ResponseEntity<>("Match completed!", headers, HttpStatus.OK);
        }
        catch(InvalidInteractionException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping(path = "/worker/reject")
    public ResponseEntity<String> rejectClient(@RequestBody MatchRequest rejectClientRequest, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            rejectClientRequest.setWorkerId(user.getId());
            matchingService.rejectClient(rejectClientRequest);
            response = new ResponseEntity<>("Client rejected!", headers, HttpStatus.OK);
        }
        catch(InvalidInteractionException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping(path = "/worker/cancelMatch")
    public ResponseEntity<String> workerCancelMatch(@RequestBody MatchRequest cancelMatchRequest, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            cancelMatchRequest.setWorkerId(user.getId());
            matchingService.cancelMatch(cancelMatchRequest);
            response = new ResponseEntity<>("Canceled Match!", headers, HttpStatus.OK);
        }
        catch(InvalidInteractionException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping(path = "/client/cancelMatch")
    public ResponseEntity<String> clientCancelMatch(@RequestBody MatchRequest cancelMatchRequest, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            cancelMatchRequest.setClientId(user.getId());
            matchingService.cancelMatch(cancelMatchRequest);
            response = new ResponseEntity<>("Canceled Match!", headers, HttpStatus.OK);
        }
        catch(InvalidInteractionException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping(path = "/client/likedOrMatchedWorkers")
    public ResponseEntity<?> clientMatchOrLikedWorkers(@AuthenticationPrincipal User user){
        ResponseEntity<?> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{

            response = new ResponseEntity<>(matchingService.getClientMatchedOrLikedWorkers(user.getId()), headers, HttpStatus.OK);
        }
        catch(InvalidClientException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping(path = "/worker/likedOrMatchedClients")
    public ResponseEntity<?> workerMatchOrLikedClients(@AuthenticationPrincipal User user){
        ResponseEntity<?> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{

            response = new ResponseEntity<>(matchingService.getWorkerLikedOrMatchedClients(user.getId()), headers, HttpStatus.OK);
        }
        catch(InvalidWorkerException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.CONFLICT);
        }

        return response;
    }


}
