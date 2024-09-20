package com.amplifiers.pathfinder.cloudstorage;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.OffsetDateTime;

@Builder
@Data
public class PresignedUrlInfo {
    private URL presignedUrl;
    private OffsetDateTime expires;
}
