package com.amplifiers.pathfinder.cloudstorage;

import lombok.Builder;

import java.net.URL;
import java.time.LocalDateTime;

@Builder
public class PresignedUrlInfo {
    public URL presignedUrl;
    public LocalDateTime expires;
}
