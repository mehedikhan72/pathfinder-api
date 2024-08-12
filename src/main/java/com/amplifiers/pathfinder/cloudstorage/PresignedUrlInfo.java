package com.amplifiers.pathfinder.cloudstorage;

import lombok.Builder;

import java.net.URL;
import java.time.OffsetDateTime;

@Builder
public class PresignedUrlInfo {
    public URL presignedUrl;
    public OffsetDateTime expires;
}
