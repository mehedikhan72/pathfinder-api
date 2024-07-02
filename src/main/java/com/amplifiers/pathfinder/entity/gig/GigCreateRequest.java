package com.amplifiers.pathfinder.entity.gig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GigCreateRequest {
    private String title;
    private String description;
    private float price;
}
