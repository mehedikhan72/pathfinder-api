package com.amplifiers.pathfinder.entity.faq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQCreateRequest {
    private String question;
    private String answer;
}
