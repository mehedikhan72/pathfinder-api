package com.amplifiers.pathfinder.entity.review;

import com.amplifiers.pathfinder.entity.gig.GigShortDTO;
import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ReviewCardDTO {
    private Integer id;
    private String title;
    private String text;
    private Short rating;
    private OffsetDateTime createdAt;
    private UserShortDTO reviewer;
    private GigShortDTO gig;
}
