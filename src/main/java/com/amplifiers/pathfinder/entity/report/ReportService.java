package com.amplifiers.pathfinder.entity.report;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.user.Role;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserUtility userUtility;
    private final EnrollmentRepository enrollmentRepository;

    public Report createReport(ReportCreateRequest reportCreateRequest) {

        User reporter = userUtility.getCurrentUser();
        User reportedUser = userRepository.findById(reportCreateRequest.getReportedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Enrollment enrollment = enrollmentRepository.findById(reportCreateRequest.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        var report = Report.builder()
                .text(reportCreateRequest.getText())
                .reporter(reporter)
                .reportedUser(reportedUser)
                .createdAt(OffsetDateTime.now())
                .enrollment(enrollment)
                .resolved(false)
                .resolvedBy(null)
                .resolvedAt(null)
                .build();
        return reportRepository.save(report);
    }

    public Page<Report> findAllUnResolvedReports(Pageable pageable) {
        User user = userUtility.getCurrentUser();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.MANAGER) {
            throw new ResourceNotFoundException("You are not authorized to view this resource");
        }
        return reportRepository.findAllByResolvedFalseOrderByCreatedAtAsc(pageable);
    }

    public Page<Report> findAllResolvedReports(Pageable pageable) {
        User user = userUtility.getCurrentUser();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.MANAGER) {
            throw new ResourceNotFoundException("You are not authorized to view this resource");
        }
        return reportRepository.findAllByResolvedTrueOrderByResolvedAtAsc(pageable);
    }

    public Report resolveReport(int reportId) {
        User user = userUtility.getCurrentUser();
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.MANAGER) {
            throw new ResourceNotFoundException("You are not authorized to view this resource");
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setResolved(true);
        report.setResolvedBy(user.getFullName());
        report.setResolvedAt(OffsetDateTime.now());
        return reportRepository.save(report);
    }
}
