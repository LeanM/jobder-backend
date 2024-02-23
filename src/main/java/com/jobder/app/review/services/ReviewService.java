package com.jobder.app.review.services;

import com.jobder.app.authentication.exceptions.InvalidClientException;
import com.jobder.app.authentication.exceptions.InvalidWorkerException;
import com.jobder.app.authentication.models.users.User;
import com.jobder.app.authentication.services.UserService;
import com.jobder.app.matching.services.MatchingService;
import com.jobder.app.review.dto.*;
import com.jobder.app.review.exceptions.ReviewException;
import com.jobder.app.review.models.Review;
import com.jobder.app.review.models.ReviewResponse;
import com.jobder.app.review.repositories.ReviewRepository;
import com.jobder.app.review.repositories.ReviewResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewResponseRepository reviewResponseRepository;
    private final UserService userService;

    private final MatchingService matchingService;

    public List<ReviewResponseDTO> getWorkerReviewsExample(String workerId) throws InvalidClientException, InvalidWorkerException {
        List<Review> reviews = reviewRepository.findByWorkerIdAndLimit(workerId,3);
        return formatReviews(reviews);
    }

    public List<ReviewResponseDTO> getAllWorkerReviews(String workerId) throws InvalidClientException, InvalidWorkerException {
        List<Review> reviews = reviewRepository.findByWorkerId(workerId);
        return formatReviews(reviews);
    }

    private List<ReviewResponseDTO> formatReviews(List<Review> reviews) throws InvalidClientException, InvalidWorkerException {
        List<ReviewResponseDTO> reviewResponseDTOS = new LinkedList<>();
        for (Review review : reviews){
            User clientMakedReview = userService.getClientById(review.getClientId());
            ClientCreatedReviewDTO clientCreatedReviewDTO = new ClientCreatedReviewDTO(clientMakedReview.getName(), clientMakedReview.getPicture());

            User workerReviewed = userService.getWorkerById(review.getWorkerId());
            WorkerReviewedDTO workerReviewedDTO = new WorkerReviewedDTO(workerReviewed.getName(), workerReviewed.getPicture());

            ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
            reviewResponseDTO.setWorker(workerReviewedDTO);
            reviewResponseDTO.setClient(clientCreatedReviewDTO);
            reviewResponseDTO.setReview(review);
            Optional<ReviewResponse> reviewResponse = reviewResponseRepository.findByReviewId(review.getId());
            if(reviewResponse.isPresent()){
                reviewResponseDTO.setReviewResponse(reviewResponse.get());
            }
            reviewResponseDTOS.add(reviewResponseDTO);
        }
        return reviewResponseDTOS;
    }

    public void addReviewToWorker(AddReviewDTO addReviewDTO) throws ReviewException, InvalidWorkerException {
        if(!matchingService.existsMatchCompletedBetweenUsers(addReviewDTO.getClientId(), addReviewDTO.getWorkerId()))
            throw new ReviewException("Users doesn't have a match completed!");

        Review newReview = new Review();
        newReview.setWorkerId(addReviewDTO.getWorkerId());
        newReview.setClientId(addReviewDTO.getClientId());
        newReview.setContent(addReviewDTO.getContent());
        newReview.setRating(addReviewDTO.getRating());
        newReview.setCreatedAt(new Date());

        userService.addWorkerReview(addReviewDTO.getWorkerId(), newReview.getRating());

        reviewRepository.save(newReview);
    }

    private String obtainAverageRating(String workerId){
        List<Review> workerReviews = reviewRepository.findByWorkerId(workerId);
        Float total = 0f;
        Float average = 1f;
        int quantity = workerReviews.size();

        for (Review review : workerReviews){
            average += review.getRating();
        }

        if(quantity > 0)
            average = total / quantity;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(average);
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
