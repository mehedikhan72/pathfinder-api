package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.UserShortDTO;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GigManageDTO {

    Integer id;
    String title;
    String gigCoverImage;
    Float price;
    boolean accepted;
    boolean paused;
    Integer score;
    Integer ongoing;
    Integer completed;
    Float earning;
    Float rating;
}
