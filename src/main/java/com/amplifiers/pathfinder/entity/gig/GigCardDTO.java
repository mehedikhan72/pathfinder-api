package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GigCardDTO {
    Integer id;
    String title;
    Set<String> tags;
    float price;
    float rating;
    int ratedByCount;
    String coverImage;
    UserShortDTO user;
}
