package com.amplifiers.pathfinder.entity.gig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GigShortDTO {
    Integer id;
    String title;
    String coverImage;
}
