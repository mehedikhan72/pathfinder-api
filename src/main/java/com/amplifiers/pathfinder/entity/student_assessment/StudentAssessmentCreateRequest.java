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
    private Integer understanding_rating;
    private Integer response_rating;
    private String feedback;
}
