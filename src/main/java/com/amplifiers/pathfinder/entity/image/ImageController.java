package com.amplifiers.pathfinder.entity.image;

import com.amplifiers.pathfinder.cloudstorage.CloudStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService service;

    @GetMapping("/{filename}")
    public ResponseEntity<?> serveImage(@PathVariable("filename") String filename) {
        try {
            Image image = service.getImageByName(filename);
            System.out.println("Served image : " + image.getFilename());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                    .body(CloudStorageService.getFile(image.getFilename()));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }

}
