package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GigCardDTO {
    private Integer id;
    private String title;
    private Set<String> tags;
    private float price;
    private float rating;
    private int ratedByCount;
    private String coverImage;
    private UserShortDTO user;
}
