package com.amplifiers.pathfinder.entity.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDTO {
    Integer id;
    String firstName;
    String lastName;
}
