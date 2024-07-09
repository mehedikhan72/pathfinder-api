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

    public StudentAssessment createStudentAssessment(StudentAssessmentCreateRequest request, Integer session_id) {
        Session session = sessionRepository.findById(session_id).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        User user = userUtility.getCurrentUser();
        User seller = session.getEnrollment().getGig().getSeller();

        if (!user.getId().equals(seller.getId())) {
            throw new UnauthorizedException("Only the seller can create a student assessment");
        }

        var student_assessment = StudentAssessment.builder().session(session).understanding_rating(request.getUnderstanding_rating()).response_rating(request.getResponse_rating()).feedback(request.getFeedback()).build();

        return studentAssessmentRepository.save(student_assessment);
    }

    public StudentAssessment getStudentAssessmentForASession(Integer session_id) {
        return studentAssessmentRepository.findBySession_Id(session_id).orElseThrow(() -> new ResourceNotFoundException("Student assessment not found"));
    }

    public List<StudentAssessment> getAllStudentAssessmentForAnEnrollment(Integer enrollment_id) {
        return studentAssessmentRepository.findAllBySession_Enrollment_Id(enrollment_id);
    }
}

