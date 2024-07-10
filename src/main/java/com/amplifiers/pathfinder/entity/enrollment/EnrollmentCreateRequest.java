package com.amplifiers.pathfinder.entity.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentCreateRequest {
    private Integer price;
    private Integer num_sessions;
    private Integer session_duration_in_minutes;
    private Integer buyer_id;
    private LocalDateTime deadline;
}
