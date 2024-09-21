package com.amplifiers.pathfinder.zoom;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ZoomAuthResponse {

    @JsonProperty(value = "access_token")
    @Column(columnDefinition = "text")
    private String accessToken;

    @JsonProperty(value = "refresh_token")
    @Column(columnDefinition = "text")
    private String refreshToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "expires_in")
    private Long expiresIn;

    //    private String scope;

    public String getAuthToken() {
        return accessToken;
    }
}
