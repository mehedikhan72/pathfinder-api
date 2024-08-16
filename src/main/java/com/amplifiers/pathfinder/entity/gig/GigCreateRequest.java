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
    private String category;
    private List<String> tags;
    private String description;
    private String offerText;
    private float price;
    private List<FAQ> faqs;
}
