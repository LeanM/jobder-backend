package com.jobder.app.review.controllers;

import com.jobder.app.authentication.models.users.User;
import com.jobder.app.matching.dto.InteractionRequest;
import com.jobder.app.matching.exceptions.InvalidInteractionException;
import com.jobder.app.review.dto.AddReviewDTO;
import com.jobder.app.review.dto.AddReviewResponseDTO;
import com.jobder.app.review.dto.ReviewResponseDTO;
import com.jobder.app.review.exceptions.ReviewException;
import com.jobder.app.review.models.Review;
import com.jobder.app.review.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping( path = "review" )
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(path = "/obtain/reviews/{workerId}")
    public ResponseEntity<?> getWorkerReviews(@PathVariable String workerId){
        ResponseEntity<?> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        response = new ResponseEntity(reviewService.getAllWorkerReviews(workerId), headers, HttpStatus.OK);

        return response;
    }

    @GetMapping(path = "/obtain/reviews/example/{workerId}")
    public ResponseEntity<?> getWorkerReviewsExample(@PathVariable String workerId){
        ResponseEntity<?> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        response = new ResponseEntity(reviewService.getWorkerReviewsExample(workerId), headers, HttpStatus.OK);

        return response;
    }

    @PostMapping(path = "/add/review")
    public ResponseEntity<String> addReviewToWorker(@RequestBody AddReviewDTO addReviewDTO, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            addReviewDTO.setClientId(user.getId());
            reviewService.addReviewToWorker(addReviewDTO);
            response = new ResponseEntity<>("Added review!", headers, HttpStatus.OK);
        }
        catch(ReviewException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PostMapping(path = "/add/reviewResponse")
    public ResponseEntity<String> addReviewResponse(@RequestBody AddReviewResponseDTO addReviewResponseDTO, @AuthenticationPrincipal User user){
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try{
            addReviewResponseDTO.setWorkerId(user.getId());
            reviewService.addReviewResponse(addReviewResponseDTO);
            response = new ResponseEntity<>("Added review response!", headers, HttpStatus.OK);
        }
        catch(ReviewException e){
            response = new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        }

        return response;
    }
}
