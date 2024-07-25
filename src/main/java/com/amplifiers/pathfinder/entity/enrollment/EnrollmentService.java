package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final GigRepository gigRepository;
    private final UserRepository userRepository;
    private final UserUtility userUtility;


    public Enrollment createEnrollment(EnrollmentCreateRequest request, Integer gig_id) {
        Gig gig = gigRepository.findById(gig_id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found."));

        var seller_id = gig.getSeller().getId();
        User user = userUtility.getCurrentUser();

        if(!Objects.equals(user.getId(), seller_id)) {
            throw new UnauthorizedException("Only the seller can create an enrollment.");
        }

        if (request.getBuyer_id() == null)
            throw new ValidationException("Buyer ID is required.");

        User buyer = userRepository.findById(request.getBuyer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found."));

        if(request.getNum_sessions() <= 0) {
            throw new ValidationException("Number of sessions must be positive.");
        }

        if(request.getSession_duration_in_minutes() <= 0) {
            throw new ValidationException("Session duration must be positive.");
        }

        if(request.getPrice() <= 0) {
            throw new ValidationException("You didn't provide a price. Are you some kind of saint?");
        }

        var enrollment = Enrollment.builder()
                .gig(gig)
                .price(request.getPrice())
                .num_sessions(request.getNum_sessions())
                .session_duration_in_minutes(request.getSession_duration_in_minutes())
                .buyer(buyer)
                .deadline(request.getDeadline())
                .num_sessions_completed(0)
                .buyer_confirmed(false)
                .paid(false)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment buyerConfirmsEnrollment(Integer enrollment_id) {
        Enrollment enrollment = enrollmentRepository.findById(enrollment_id).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        User user = userUtility.getCurrentUser();
        User buyer = enrollment.getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm an enrollment.");
        }

        enrollment.setBuyer_confirmed(true);
        return enrollmentRepository.save(enrollment);
    }

    // TODO: Update enrollment.
    // TODO: Cancel enrollment.

    public List<Enrollment> findAllByGigId(Integer id) {
        return enrollmentRepository.findAllByGigId(id);
    }

    public List<Enrollment> findAllByBuyerId(Integer id) {
        return enrollmentRepository.findAllByBuyerId(id);
    }

    public boolean hasDeadlinePassed(Integer id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(enrollment.getDeadline());
    }
}
