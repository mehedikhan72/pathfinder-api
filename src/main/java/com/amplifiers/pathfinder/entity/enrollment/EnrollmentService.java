package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final GigRepository gigRepository;
    private final UserRepository userRepository;


    public Enrollment createEnrollment(EnrollmentCreateRequest request, Integer gig_id) {
        Gig gig = gigRepository.findById(gig_id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        User buyer = userRepository.findById(request.getBuyer_id())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        var enrollment = Enrollment.builder()
                .gig(gig)
                .price(request.getPrice())
                .num_sessions(request.getNum_sessions())
                .session_duration_in_minutes(request.getSession_duration_in_minutes())
                .buyer(buyer)
                .num_sessions_completed(0)
                .buyer_confirmed(false)
                .paid(false)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> findAllByGigId(Integer id) {
        return enrollmentRepository.findAllByGigId(id);
    }

    public List<Enrollment> findAllByBuyerId(Integer id) {
        return enrollmentRepository.findAllByBuyerId(id);
    }
}
