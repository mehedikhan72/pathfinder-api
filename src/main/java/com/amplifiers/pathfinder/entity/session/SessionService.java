package com.amplifiers.pathfinder.entity.session;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final UserUtility userUtility;

    public Session createSession(SessionCreateRequest request, Integer enrollment_id) {
        Enrollment enrollment = enrollmentRepository.findById(enrollment_id).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        // user validation: only the seller can create a session.
        var seller_id = enrollment.getGig().getSeller().getId();

        User user = userUtility.getCurrentUser();

        if (!Objects.equals(user.getId(), seller_id)) {
            throw new UnauthorizedException("Only the seller can create a session.");
        }

        var session = Session.builder().enrollment(enrollment).scheduled_at(request.getScheduled_at()).session_type(request.getSession_type()).buyer_confirmed(false).completed(false).build();

        return sessionRepository.save(session);
    }

    public Session buyerConfirmsSession(Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        // only the buyer can confirm a session
        User user = userUtility.getCurrentUser();
        User buyer = session.getEnrollment().getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm a session.");
        }

        session.setBuyer_confirmed(true);
        return sessionRepository.save(session);
    }

    public Session updateSession(SessionCreateRequest request, Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        // TODO: for now, let's allow only the seller to update the session

        var seller = session.getEnrollment().getGig().getSeller();
        var user = userUtility.getCurrentUser();

        if (!Objects.equals(seller.getId(), user.getId())) {
            throw new UnauthorizedException("Only the seller can update the session.");
        }

        session.setScheduled_at(request.getScheduled_at());
        session.setSession_type(request.getSession_type());
        return sessionRepository.save(session);
    }

    public Session completeSession(Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        // only the seller can complete a session
        var seller = session.getEnrollment().getGig().getSeller();
        var user = userUtility.getCurrentUser();
        if (!Objects.equals(seller.getId(), user.getId())) {
            throw new UnauthorizedException("Only the seller can complete a session.");
        }

        if (session.isCancelled()) {
            throw new ValidationException("Session is already cancelled.");
        }

        if (session.isCompleted()) {
            throw new ValidationException("Session already completed.");
        }

        if (!session.isBuyer_confirmed()) {
            throw new ValidationException("Buyer has not confirmed the session.");
        }

        session.setCompleted(true);
        session.setCompleted_at(java.time.LocalDateTime.now());

        // update relevant enrollment info.
        Enrollment enrollment = session.getEnrollment();
        enrollment.setNum_sessions_completed(enrollment.getNum_sessions_completed() + 1);

        if(Objects.equals(enrollment.getNum_sessions(), enrollment.getNum_sessions_completed())) {
            enrollment.setCompleted_at(java.time.LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);
        return sessionRepository.save(session);
    }

    public String cancelSession(SessionCancelRequest request, Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        User user = userUtility.getCurrentUser();
        User seller = session.getEnrollment().getGig().getSeller();
        User buyer = session.getEnrollment().getBuyer();

        if (!Objects.equals(user.getId(), seller.getId()) && !Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the seller or the buyer can cancel this session.");
        }

        if (session.isCompleted()) {
            throw new ValidationException("Session is already completed.");
        }

        if (session.isCancelled()) {
            throw new ValidationException("Session is already cancelled.");
        }

        session.setCancelled(true);

        if (Objects.equals(user.getId(), seller.getId())) {
            session.setCancelled_by(CancelledBy.SELLER);
        } else {
            session.setCancelled_by(CancelledBy.BUYER);
        }

        if (request.getCancellation_reason() == null || request.getCancellation_reason().isBlank()) {
            throw new ValidationException("Cancellation reason is required.");
        }

        session.setCancellation_reason(request.getCancellation_reason());
        session.setCancelled_at(java.time.LocalDateTime.now());

        sessionRepository.save(session);

        return "Session cancelled.";
    }
}
