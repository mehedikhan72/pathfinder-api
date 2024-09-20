package com.amplifiers.pathfinder.entity.student_assessment;

import com.amplifiers.pathfinder.entity.session.Session;
import com.amplifiers.pathfinder.entity.session.SessionRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.UnauthorizedException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAssessmentService {
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final SessionRepository sessionRepository;
    private final UserUtility userUtility;

    public StudentAssessment createStudentAssessment(StudentAssessmentCreateRequest request, Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        User user = userUtility.getCurrentUser();
        User seller = session.getEnrollment().getGig().getSeller();

        if (!user.getId().equals(seller.getId())) {
            throw new UnauthorizedException("Only the seller can create a student assessment");
        }

        var studentAssessment = StudentAssessment.builder()
                .session(session)
                .understandingRating(request.getUnderstandingRating())
                .responseRating(request.getResponseRating())
                .feedback(request.getFeedback()).build();

        return studentAssessmentRepository.save(studentAssessment);
    }

    public StudentAssessment getStudentAssessmentForASession(Integer sessionId) {
        return studentAssessmentRepository.findBySessionId(sessionId).orElseThrow(() -> new ResourceNotFoundException("Student assessment not found"));
    }

    public List<StudentAssessment> getAllStudentAssessmentForAnEnrollment(Integer enrollmentId) {
        return studentAssessmentRepository.findAllBySession_Enrollment_Id(enrollmentId);
    }
}

