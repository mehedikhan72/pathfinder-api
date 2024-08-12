package com.amplifiers.pathfinder.entity.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {
    private OffsetDateTime scheduledAt;
    private String sessionType;
}
