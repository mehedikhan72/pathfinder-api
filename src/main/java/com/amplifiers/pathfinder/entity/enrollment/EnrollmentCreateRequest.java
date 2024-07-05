package com.amplifiers.pathfinder.entity.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentCreateRequest {
    private Integer price;
    private Integer num_sessions;
    private Integer buyer_id;
}
