package com.amplifiers.pathfinder.entity.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportCreateRequest {
    private String text;
    private Integer reportedUserId;
    private Integer enrollmentId;
}
