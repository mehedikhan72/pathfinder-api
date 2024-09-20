package com.amplifiers.pathfinder.entity.user;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    private String title;
    private Integer year;
}
