package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class GigPageDTO {
    private Integer id;
    private String title;
    private String category;
    private String description;
    private String offerText;
    private float price;
    private String gigCoverImage;
    private String gigVideo;
    private List<String> tags;
    private List<FAQ> faqs;
    private float rating;
    private Integer totalReviews;
    private Integer totalCompleted;
    private Integer totalOrders;
    private boolean accepted;
    private UserShortDTO seller;
    private OffsetDateTime createdAt;
}
