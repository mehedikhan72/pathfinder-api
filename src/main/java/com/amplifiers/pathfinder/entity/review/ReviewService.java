package com.amplifiers.pathfinder.entity.review;

import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.gig.GigService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import com.amplifiers.pathfinder.exception.ForbiddenException;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository repository;
    private final GigRepository gigRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserUtility userUtility;

    private void validateReviewRequest(ReviewRequest reviewRequest) {
        if (reviewRequest.getRating() < 0 || reviewRequest.getRating() > 5) {
            throw new ValidationException("Rating must be between 0 and 5");
        }
    }

    public Review createReview(Integer gigId, ReviewRequest reviewRequest) {
        User user = userUtility.getCurrentUser();

        if (!gigRepository.existsById(gigId)) {
            throw new ResourceNotFoundException("Gig does not exist");
        }

        Gig gig = gigRepository.getReferenceById(gigId);

        if (!enrollmentRepository.existsByGigIdAndBuyerAndCompletedAtNotNull(gigId, user)) {
            throw new ForbiddenException("Cannot write review without completing the gig");
        }

        validateReviewRequest(reviewRequest);

        Review review = Review.builder()
                .title(reviewRequest.getTitle())
                .text(reviewRequest.getText())
                .rating(reviewRequest.getRating())
                .createdAt(LocalDateTime.now())
                .reviewer(user)
                .gig(gig)
                .build();

        return repository.save(review);
    }

    public List<Review> findAllByGigId(Integer gigId) {
        if (!gigRepository.existsById(gigId)) {
            throw new ResourceNotFoundException("Gig does not exist");
        }

        return repository.findAllByGigId(gigId);
    }

    private void validateReviewPermission(Integer gigId, Integer reviewId) {
        if (!gigRepository.existsById(gigId)) {
            throw new ResourceNotFoundException("Gig does not exist");
        }

        if (!repository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review does not exist");
        }

        if (!repository.existsByIdAndGigId(reviewId, gigId)) {
            throw new ForbiddenException("This review is not under this gig");
        }

        User user = userUtility.getCurrentUser();

        if (!repository.existsByIdAndReviewer(reviewId, user)) {
            throw new ForbiddenException("Review not written by this user");
        }
    }

    public void deleteReview(Integer gigId, Integer reviewId) {
        validateReviewPermission(gigId, reviewId);

        repository.deleteById(reviewId);

        System.out.println("Review id " + reviewId + " successfully deleted");
    }

    public Review editReview(Integer gigId, Integer reviewId, ReviewRequest reviewRequest) {
        validateReviewPermission(gigId, reviewId);
        validateReviewRequest(reviewRequest);

        Review review = repository.getReferenceById(reviewId);

        if (reviewRequest.getTitle() != null)
            review.setTitle(reviewRequest.getTitle());
        if (reviewRequest.getText() != null)
            review.setText(reviewRequest.getText());
        if (reviewRequest.getRating() != null)
            review.setRating(reviewRequest.getRating());

        return repository.save(review);
    }

    public static ReviewCardDTO createReviewCardDTO(Review review, boolean includeGig) {
        UserShortDTO reviewer = UserShortDTO.builder()
                .id(review.getReviewer().getId())
                .firstName(review.getReviewer().getFirstName())
                .lastName(review.getReviewer().getLastName())
                .build();

        var reviewCardDTOBuilder = ReviewCardDTO.builder()
                .id(review.getId())
                .title(review.getTitle())
                .text(review.getText())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .reviewer(reviewer);

        if (includeGig) {
            reviewCardDTOBuilder = reviewCardDTOBuilder.gig(GigService.createGigShortDTO(review.getGig()));
        }

        return reviewCardDTOBuilder.build();
    }

    public static ReviewCardDTO createReviewCardDTO(Review review) {
        return createReviewCardDTO(review, true);
    }
    public List<ReviewCardDTO> getReviewCardsBySellerId(Integer sellerId) {
        List<Review> reviews = repository.findAllReviewsBySellerId(sellerId);

        List<ReviewCardDTO> reviewCardDTOS = reviews.stream().map(r -> createReviewCardDTO(r)).toList();

        return reviewCardDTOS;
    }


}
