package com.amplifiers.pathfinder.entity.gig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GigCreateRequest {
    private String title;
    private String description;
    private float price;
    private String category;
    private List<String> tags;
}
