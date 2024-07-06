package com.amplifiers.pathfinder.entity.session;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        Enrollment enrollment = enrollmentRepository.findById(enrollment_id).orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        // user validation: only the seller can create a session.
        var seller_id = enrollment.getGig().getSeller().getId();

        User user = userUtility.getCurrentUser();

        if (!Objects.equals(user.getId(), seller_id)) {
            throw new UnauthorizedException("Only the seller can create a session");
        }

        var session = Session.builder().enrollment(enrollment).scheduled_at(request.getScheduled_at()).session_type(request.getSession_type()).buyer_confirmed(false).completed(false).build();

        return sessionRepository.save(session);
    }

    public Session buyerConfirmsSession(Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // only the buyer can confirm a session
        User user = userUtility.getCurrentUser();
        User buyer = session.getEnrollment().getBuyer();

        if (!Objects.equals(user.getId(), buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm a session");
        }

        session.setBuyer_confirmed(true);
        return sessionRepository.save(session);
    }

    public Session updateSession(SessionCreateRequest request, Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // TODO: for now, let's allow only the seller to update the session

        var seller = session.getEnrollment().getGig().getSeller();
        var user = userUtility.getCurrentUser();

        if (!Objects.equals(seller.getId(), user.getId())) {
            throw new UnauthorizedException("Only the seller can update the session");
        }

        session.setScheduled_at(request.getScheduled_at());
        session.setSession_type(request.getSession_type());
        return sessionRepository.save(session);
    }

    public Session completeSession(Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // only the seller can complete a session
        var seller = session.getEnrollment().getGig().getSeller();
        var user = userUtility.getCurrentUser();
        if (!Objects.equals(seller.getId(), user.getId())) {
            throw new UnauthorizedException("Only the seller can complete a session");
        }

        session.setCompleted(true);
        session.setCompleted_at(java.time.LocalDateTime.now());
        return sessionRepository.save(session);
    }
}
