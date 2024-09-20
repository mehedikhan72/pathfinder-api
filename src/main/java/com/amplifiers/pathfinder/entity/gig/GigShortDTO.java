package com.amplifiers.pathfinder.entity.gig;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GigShortDTO {
    private Integer id;
    private String title;
    private String coverImage;
}
