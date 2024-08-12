package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.notification.NotificationCreateRequest;
import com.amplifiers.pathfinder.entity.notification.NotificationService;
import com.amplifiers.pathfinder.entity.notification.NotificationType;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final GigRepository gigRepository;
    private final UserRepository userRepository;
    private final UserUtility userUtility;
    private final NotificationService notificationService;

    // TODO: update later - there can be only one concurrent enrollment between a buyer and seller. the
    // TODO: seller won't be able to initiate any new offers to the same buyer.

    public Enrollment createEnrollment(EnrollmentCreateRequest request, Integer gigId) {
        Gig gig = gigRepository.findById(gigId)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found."));

        var sellerId = gig.getSeller().getId();
        User user = userUtility.getCurrentUser();

        if (!Objects.equals(user.getId(), sellerId)) {
            throw new UnauthorizedException("Only the seller can create an enrollment.");
        }

        if (request.getBuyerId() == null)
            throw new ValidationException("Buyer ID is required.");

        // since only one incomplete enrollment can exist at a time.
        Optional<Enrollment> existingEnrollment = findIncompleteEnrollmentBySellerIdAndBuyerId(sellerId, request.getBuyerId());
        if (existingEnrollment.isPresent()) {
            throw new ValidationException("An incomplete enrollment already exists between you and this buyer. Please complete this one first.");
        }

        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found."));

        if (request.getNumSessions() <= 0) {
            throw new ValidationException("Number of sessions must be positive.");
        }

        if (request.getSessionDurationInMinutes() <= 0) {
            throw new ValidationException("Session duration must be positive.");
        }

        if (request.getPrice() <= 0) {
            throw new ValidationException("You didn't provide a price. Are you some kind of saint?");
        }

        var enrollment = Enrollment.builder()
                .gig(gig)
                .price(request.getPrice())
                .numSessions(request.getNumSessions())
                .sessionDurationInMinutes(request.getSessionDurationInMinutes())
                .buyer(buyer)
                .deadline(request.getDeadline())
                .numSessionsCompleted(0)
                .buyerConfirmed(false)
                .paid(false)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        String notificationTxt = savedEnrollment.getGig().getSeller().getFullName()
                + " has offered you a new enrollment.";
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .text(notificationTxt)
                .receiver(savedEnrollment.getBuyer())
                .type(NotificationType.ENROLLMENT)
                // interaction/user/{id} is a link to the interaction page where id is the
                // user id of the person im talking to.
                .linkSuffix("interaction/user/" + savedEnrollment.getGig().getSeller().getId())
                .build();

        notificationService.createNotification(notificationCreateRequest);
        return savedEnrollment;
    }

    public Enrollment buyerConfirmsEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        User user = userUtility.getCurrentUser();
        User buyer = enrollment.getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm an enrollment.");
        }

        enrollment.setBuyerConfirmed(true);
        return enrollmentRepository.save(enrollment);
    }

    // TODO: Update enrollment.
    // TODO: Cancel enrollment.

    public Optional<Enrollment> findIncompleteEnrollmentBySellerIdAndBuyerId(Integer sellerId, Integer buyerId) {
        return enrollmentRepository.findIncompleteEnrollmentBySellerIdAndBuyerId(sellerId, buyerId);
    }

    public Page<Enrollment> findAllByGigId(Pageable pageable, Integer id) {
        return enrollmentRepository.findAllByGigId(pageable, id);
    }

    public Page<Enrollment> findAllByBuyerId(Pageable pageable, Integer id) {
        return enrollmentRepository.findAllByBuyerId(pageable, id);
    }

    public boolean hasDeadlinePassed(Integer id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        OffsetDateTime now = OffsetDateTime.now();
        return now.isAfter(enrollment.getDeadline());
    }
}
