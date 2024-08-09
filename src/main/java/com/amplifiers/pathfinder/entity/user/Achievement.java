package com.amplifiers.pathfinder.entity.user;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Year;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    String title;
    Integer year;
}
