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

    public Session createSession(SessionCreateRequest request, Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        // user validation: only the seller can create a session.
        var sellerId = enrollment.getGig().getSeller().getId();

        User user = userUtility.getCurrentUser();

        if (!Objects.equals(user.getId(), sellerId)) {
            throw new UnauthorizedException("Only the seller can create a session.");
        }

        var session = Session.builder().enrollment(enrollment).scheduledAt(request.getScheduledAt()).sessionType(request.getSessionType()).buyerConfirmed(false).completed(false).build();

        return sessionRepository.save(session);
    }

    public Session buyerConfirmsSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        // only the buyer can confirm a session
        User user = userUtility.getCurrentUser();
        User buyer = session.getEnrollment().getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm a session.");
        }

        session.setBuyerConfirmed(true);
        return sessionRepository.save(session);
    }

    public Session updateSession(SessionCreateRequest request, Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        // TODO: for now, let's allow only the seller to update the session

        var seller = session.getEnrollment().getGig().getSeller();
        var user = userUtility.getCurrentUser();

        if (!Objects.equals(seller.getId(), user.getId())) {
            throw new UnauthorizedException("Only the seller can update the session.");
        }

        session.setScheduledAt(request.getScheduledAt());
        session.setSessionType(request.getSessionType());
        return sessionRepository.save(session);
    }

    public Session completeSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

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

        if (!session.isBuyerConfirmed()) {
            throw new ValidationException("Buyer has not confirmed the session.");
        }

        session.setCompleted(true);
        session.setCompletedAt(java.time.LocalDateTime.now());

        // update relevant enrollment info.
        Enrollment enrollment = session.getEnrollment();
        enrollment.setNumSessionsCompleted(enrollment.getNumSessionsCompleted() + 1);

        if(Objects.equals(enrollment.getNumSessions(), enrollment.getNumSessionsCompleted())) {
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);
        return sessionRepository.save(session);
    }

    public String cancelSession(SessionCancelRequest request, Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

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
            session.setCancelledBy(CancelledBy.SELLER);
        } else {
            session.setCancelledBy(CancelledBy.BUYER);
        }

        if (request.getCancellationReason() == null || request.getCancellationReason().isBlank()) {
            throw new ValidationException("Cancellation reason is required.");
        }

        session.setCancellationReason(request.getCancellationReason());
        session.setCancelledAt(java.time.LocalDateTime.now());

        sessionRepository.save(session);

        return "Session cancelled.";
    }
}
