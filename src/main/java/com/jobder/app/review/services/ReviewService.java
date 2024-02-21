package com.jobder.app.review.services;

import com.jobder.app.matching.services.MatchingService;
import com.jobder.app.review.dto.AddReviewDTO;
import com.jobder.app.review.dto.AddReviewResponseDTO;
import com.jobder.app.review.dto.ReviewResponseDTO;
import com.jobder.app.review.exceptions.ReviewException;
import com.jobder.app.review.models.Review;
import com.jobder.app.review.models.ReviewResponse;
import com.jobder.app.review.repositories.ReviewRepository;
import com.jobder.app.review.repositories.ReviewResponseRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewResponseRepository reviewResponseRepository;

    private final MatchingService matchingService;

    public List<ReviewResponseDTO> getWorkerReviewsExample(String workerId){
        List<Review> reviews = reviewRepository.findByWorkerIdAndLimit(workerId,3);
        return formatReviews(reviews);
    }

    public List<ReviewResponseDTO> getAllWorkerReviews(String workerId){
        List<Review> reviews = reviewRepository.findByWorkerId(workerId);
        return formatReviews(reviews);
    }

    private List<ReviewResponseDTO> formatReviews(List<Review> reviews){
        List<ReviewResponseDTO> reviewResponseDTOS = new LinkedList<>();
        for (Review review : reviews){
            ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
            reviewResponseDTO.setReview(review);
            Optional<ReviewResponse> reviewResponse = reviewResponseRepository.findByReviewId(review.getId());
            if(reviewResponse.isPresent()){
                reviewResponseDTO.setReviewResponse(reviewResponse.get());
            }
            reviewResponseDTOS.add(reviewResponseDTO);
        }
        return reviewResponseDTOS;
    }

    public void addReviewToWorker(AddReviewDTO addReviewDTO) throws ReviewException {
        if(!matchingService.existsMatchBetweenUsers(addReviewDTO.getClientId(), addReviewDTO.getWorkerId()))
            throw new ReviewException("Users doesnt have a match!");

        Review newReview = new Review();
        newReview.setWorkerId(addReviewDTO.getWorkerId());
        newReview.setClientId(addReviewDTO.getClientId());
        newReview.setContent(addReviewDTO.getContent());
        newReview.setRating(addReviewDTO.getRating());
        newReview.setCreatedAt(new Date());

        reviewRepository.save(newReview);
    }

    public void addReviewResponse(AddReviewResponseDTO addReviewResponseDTO) throws ReviewException {
        Review existingReview = reviewRepository.findById(addReviewResponseDTO.getReviewId()).orElseThrow( () -> new ReviewException("The review doesn't exists!"));
        if(!existingReview.getWorkerId().equals(addReviewResponseDTO.getWorkerId()))
            throw new ReviewException("The review doesn't belong to the worker!");

        if(reviewResponseRepository.findByReviewId(addReviewResponseDTO.getReviewId()).isPresent())
            throw new ReviewException("The review already has a review response!");

        ReviewResponse newReviewResponse = new ReviewResponse();
        newReviewResponse.setReviewId(addReviewResponseDTO.getReviewId());
        newReviewResponse.setContent(addReviewResponseDTO.getContent());
        newReviewResponse.setWorkerId(addReviewResponseDTO.getWorkerId());
        newReviewResponse.setCreatedAt(new Date());

        reviewResponseRepository.save(newReviewResponse);
    }
}
