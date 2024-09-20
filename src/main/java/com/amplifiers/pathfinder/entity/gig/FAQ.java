package com.amplifiers.pathfinder.entity.gig;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FAQ {
    private String question;
    private String answer;
}
