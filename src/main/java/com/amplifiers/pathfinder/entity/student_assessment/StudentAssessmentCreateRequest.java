package com.amplifiers.pathfinder.entity.student_assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentAssessmentCreateRequest {
    private Integer understandingRating;
    private Integer responseRating;
    private String feedback;
}
