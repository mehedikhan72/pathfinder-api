package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private LocalDateTime createdAt;
}
