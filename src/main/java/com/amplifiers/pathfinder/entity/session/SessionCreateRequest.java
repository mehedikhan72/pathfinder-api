package com.amplifiers.pathfinder.entity.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {
    private LocalDateTime scheduledAt;
    private String sessionType;
}
