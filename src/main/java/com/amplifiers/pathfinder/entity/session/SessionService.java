package com.amplifiers.pathfinder.entity.session;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.notification.NotificationService;
import com.amplifiers.pathfinder.entity.notification.NotificationType;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserUtility userUtility;
    private final NotificationService notificationService;

    public Session createSession(SessionCreateRequest request, Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        // user validation: only the seller can create a session.
        var sellerId = enrollment.getGig().getSeller().getId();

        User user = userUtility.getCurrentUser();

        if (!Objects.equals(user.getId(), sellerId)) {
            throw new UnauthorizedException("Only the seller can create a session.");
        }

        // if a session is running, then the seller can't create a new session.
        Optional<Session> runningSession = findRunningSessionByEnrollmentId(enrollmentId);
        if (runningSession.isPresent()) {
            throw new ValidationException("A session is already running for this enrollment.");
        }

        // make sure schduled time is in the future
        if (request.getScheduledAt().isBefore(OffsetDateTime.now())) {
            throw new ValidationException("Scheduled time must be in the future.");
        }

        var session = Session.builder()
                .enrollment(enrollment)
                .scheduledAt(request.getScheduledAt())
                .sessionType(request.getSessionType())
                .buyerConfirmed(false)
                .completed(false)
                .createdAt(OffsetDateTime.now())
                .build();

        Session savedSession = sessionRepository.save(session);
        // Send notification
        String notificationTxt = session.getEnrollment().getGig().getSeller().getFullName() + " has scheduled a session for you. It's waiting for your confirmation.";
        String linkSuffix = "interaction/user/" + session.getEnrollment().getGig().getSeller().getId();
        notificationService.sendNotification(notificationTxt, session.getEnrollment().getBuyer(), NotificationType.SESSION, linkSuffix);

        return savedSession;
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

        // Send notification
        String notificationTxt = session.getEnrollment().getBuyer().getFullName() + " has confirmed the session.";
        String linkSuffix = "interaction/user/" + session.getEnrollment().getBuyer().getId();
        notificationService.sendNotification(notificationTxt, session.getEnrollment().getGig().getSeller(), NotificationType.SESSION, linkSuffix);

        return sessionRepository.save(session);
    }

    public String buyerDeclinesSession(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        // only the buyer can decline a session
        User user = userUtility.getCurrentUser();
        User buyer = session.getEnrollment().getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can decline a session.");
        }

        sessionRepository.delete(session);

        // Send notification
        String notificationTxt = session.getEnrollment().getBuyer().getFullName() + " has declined the session.";
        String linkSuffix = "interaction/user/" + session.getEnrollment().getBuyer().getId();
        notificationService.sendNotification(notificationTxt, session.getEnrollment().getGig().getSeller(), NotificationType.SESSION, linkSuffix);
        return "deleted";
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
        session.setCompletedAt(java.time.OffsetDateTime.now());

        // update relevant enrollment info.
        Enrollment enrollment = session.getEnrollment();
        enrollment.setNumSessionsCompleted(enrollment.getNumSessionsCompleted() + 1);

        if (enrollment.getNumSessions() <= enrollment.getNumSessionsCompleted()) {
            enrollment.setCompletedAt(java.time.OffsetDateTime.now());
        }
        enrollmentRepository.save(enrollment);

        // Send notification
        String notificationTxt = session.getEnrollment().getGig().getSeller().getFullName() + " has marked one session completed.";
        String linkSuffix = "interaction/user/" + session.getEnrollment().getGig().getSeller().getId();
        notificationService.sendNotification(notificationTxt, session.getEnrollment().getBuyer(), NotificationType.SESSION, linkSuffix);
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
        session.setCancelledAt(java.time.OffsetDateTime.now());

        sessionRepository.save(session);

        return "Session cancelled.";
    }

    public boolean userPartOfEnrollment(Integer userId, Integer enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));
        return Objects.equals(enrollment.getBuyer().getId(), userId) || Objects.equals(enrollment.getGig().getSeller().getId(), userId);
    }

    public Optional<Session> findRunningSessionByEnrollmentId(Integer EnrollmentId) {
        User user = userUtility.getCurrentUser();
        if(userPartOfEnrollment(user.getId(), EnrollmentId)) {
            return sessionRepository.findRunningSessionByEnrollmentId(EnrollmentId);
        }
        throw new UnauthorizedException("You are not part of this enrollment.");
    }

    public List<Session> findAllByEnrollmentId(Integer enrollmentId) {
        User user = userUtility.getCurrentUser();
        if(userPartOfEnrollment(user.getId(), enrollmentId)) {
            return sessionRepository.findAllByEnrollmentId(enrollmentId);
        }
        throw new UnauthorizedException("You are not part of this enrollment.");
    }
}
