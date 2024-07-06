package com.amplifiers.pathfinder.entity.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionCancelRequest {
    private String cancellation_reason;
}
