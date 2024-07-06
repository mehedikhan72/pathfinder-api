package com.amplifiers.pathfinder.entity.gig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GigImageSetRequest {
    private Integer id;
    private MultipartFile image;
}
