package com.amplifiers.pathfinder.entity.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDTO {
    private Integer id;
    private String firstName;
    private String lastName;
}
