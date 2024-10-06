package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.notification.NotificationService;
import com.amplifiers.pathfinder.entity.notification.NotificationType;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.payment.PaymentService;
import com.amplifiers.pathfinder.utility.EmailService;
import com.amplifiers.pathfinder.utility.UserUtility;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final GigRepository gigRepository;
    private final UserRepository userRepository;
    private final UserUtility userUtility;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final Integer scoreIncreaseOnEnrollmentCreation = 5;

    // TODO: update later - there can be only one concurrent enrollment between a buyer and seller. the
    // TODO: seller won't be able to initiate any new offers to the same buyer.

    public Enrollment createEnrollment(EnrollmentCreateRequest request, Integer gigId) {
        Gig gig = gigRepository.findById(gigId).orElseThrow(() -> new ResourceNotFoundException("Gig not found."));

        var sellerId = gig.getSeller().getId();
        User user = userUtility.getCurrentUser();

        if (!Objects.equals(user.getId(), sellerId)) {
            throw new UnauthorizedException("Only the seller can create an enrollment.");
        }

        if (request.getBuyerId() == null) {
            throw new ValidationException("Buyer ID is required.");
        }

        // since only one incomplete enrollment can exist at a time.
        Optional<Enrollment> existingEnrollment = findIncompleteEnrollmentBySellerIdAndBuyerId(sellerId, request.getBuyerId());
        if (existingEnrollment.isPresent()) {
            throw new ValidationException(
                "An incomplete enrollment already exists between you and this buyer. Please complete this one first."
            );
        }

        User buyer = userRepository.findById(request.getBuyerId()).orElseThrow(() -> new ResourceNotFoundException("Buyer not found."));

        if (request.getNumSessions() <= 0) {
            throw new ValidationException("Number of sessions must be positive.");
        }

        if (request.getSessionDurationInMinutes() <= 0) {
            throw new ValidationException("Session duration must be positive.");
        }

        if (request.getPrice() <= 0) {
            throw new ValidationException("You didn't provide a price. Are you some kind of saint?");
        }

        if (request.getDeadline().isBefore(OffsetDateTime.now())) {
            throw new ValidationException("Deadline must be in the future.");
        }

        var enrollment = Enrollment.builder()
            .gig(gig)
            .price(Float.valueOf(request.getPrice()))
            .numSessions(request.getNumSessions())
            .sessionDurationInMinutes(request.getSessionDurationInMinutes())
            .buyer(buyer)
            .deadline(request.getDeadline())
            .numSessionsCompleted(0)
            .buyerConfirmed(false)
            .paid(false)
            .createdAt(OffsetDateTime.now())
            .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        gig.setScore(gig.getScore() + scoreIncreaseOnEnrollmentCreation);
        gigRepository.save(gig);

        User seller = savedEnrollment.getGig().getSeller();

        // Sending notification to receiver
        String notificationTemplate = "%s - %s - Offered you a new enrollment.";
        String notificationTxt = String.format(notificationTemplate, seller.getFullName(), gig.getTitle());
        String linkSuffix = "interaction/user/" + seller.getId();
        notificationService.sendNotification(notificationTxt, savedEnrollment.getBuyer(), NotificationType.ENROLLMENT, linkSuffix);

        // sending email to buyer
        try {
            emailService.sendEmail(savedEnrollment.getBuyer(),
                    "New Enrollment Offer",
                    "You have received a new enrollment offer from " + savedEnrollment.getGig().getSeller().getFullName()
                            + ".\n"
                            + "Please visit the website to view the offer and confirm it.\n\n"
                            + "Best,\n"
                            + "Team pathPhindr\n");
        } catch (Exception e) {
            throw new ValidationException("Email could not be sent. Please try again.");
        }

        return savedEnrollment;
    }

    public String buyerConfirmsEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        User user = userUtility.getCurrentUser();
        User buyer = enrollment.getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm an enrollment.");
        }

        if (enrollment.isPaid() && enrollment.isBuyerConfirmed() && enrollment.getStartedAt() != null) {
            throw new ValidationException("You have already paid and confirmed this enrollment.");
        }

        // TODO: handle payment here.
        String url;
        try {
            url = paymentService.handleOnlinePayment(enrollment);
        } catch (Exception e) {
            throw new ValidationException("Payment failed. Please try again.");
        }

        return url;

        // Info: for now, payment bypassed.
//        enrollment.setPaid(true);
//
//        enrollment.setBuyerConfirmed(true);
//        enrollment.setStartedAt(OffsetDateTime.now());
//
//        // Sending notification
//        String notificationTxt = enrollment.getBuyer().getFullName()
//                + " has accepted your enrollment offer.";
//        String linkSuffix = "interaction/user/" + enrollment.getBuyer().getId();
//        notificationService.sendNotification(notificationTxt, enrollment.getGig().getSeller(), NotificationType.ENROLLMENT, linkSuffix);
//        return enrollmentRepository.save(enrollment);
    }

    public void buyerDeclinesEnrollment(Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        User user = userUtility.getCurrentUser();
        User buyer = enrollment.getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can decline an enrollment.");
        }

        // Sending notification
        String notificationTemplate = "%s - %s - Declined your enrollment offer.";
        String notificationTxt = String.format(notificationTemplate, enrollment.getBuyer().getFullName(), enrollment.getGig().getTitle());
        String linkSuffix = "interaction/user/" + enrollment.getBuyer().getId();
        notificationService.sendNotification(notificationTxt, enrollment.getGig().getSeller(), NotificationType.ENROLLMENT, linkSuffix);

        enrollmentRepository.delete(enrollment);
    }

    // TODO: Update enrollment.
    // TODO: Cancel enrollment.

    public Optional<Enrollment> findIncompleteEnrollmentBySellerIdAndBuyerId(Integer sellerId, Integer buyerId) {
        return enrollmentRepository.findIncompleteEnrollmentBySellerIdAndBuyerId(sellerId, buyerId);
    }

    //    public Optional<Enrollment> findRunningEnrollmentBetweenTwoUsers(Integer userId1, Integer userId2) {
    //        return enrollmentRepository.findRunningEnrollmentBetweenTwoUsers(userId1, userId2);
    //    }

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

    public Enrollment findById(Integer id) {
        User user = userUtility.getCurrentUser();

        // make sure the user is either the buyer or the seller as this is intended for the enrollment details view.
        return enrollmentRepository
            .findById(id)
            .filter(
                enrollment ->
                    Objects.equals(user.getId(), enrollment.getBuyer().getId()) ||
                    Objects.equals(user.getId(), enrollment.getGig().getSeller().getId())
            )
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));
    }
}
