package com.amplifiers.pathfinder.entity.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentCreateRequest {
    private Integer price;
    private Integer numSessions;
    private Integer sessionDurationInMinutes;
    private Integer buyerId;
    private OffsetDateTime deadline;
}
