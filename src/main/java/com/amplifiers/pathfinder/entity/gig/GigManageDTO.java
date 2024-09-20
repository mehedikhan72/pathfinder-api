package com.amplifiers.pathfinder.entity.gig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GigManageDTO {
    private Integer id;
    private String title;
    private String gigCoverImage;
    private Float price;
    private boolean accepted;
    private boolean paused;
    private Integer score;
    private Integer ongoing;
    private Integer completed;
    private Float earning;
    private Float rating;
}
