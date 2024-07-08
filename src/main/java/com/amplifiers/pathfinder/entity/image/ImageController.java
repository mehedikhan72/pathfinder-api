package com.amplifiers.pathfinder.entity.image;

import com.amplifiers.pathfinder.cloudstorage.CloudStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/public/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService service;

    @PostMapping("/save")
    public ResponseEntity<?> saveImage(@RequestParam("image") MultipartFile file){
        try {
            return ResponseEntity.ok(service.saveImage(file));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }

    @RequestMapping("/{filename}")
    public ResponseEntity<?> serveImage(@PathVariable("filename") String filename) {
        try {
            Image image = service.getImage(filename);
            System.out.println("Served image : "+image.getFilename());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(CloudStorageService.getFile(image.getFilename()));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }

    @RequestMapping("/delete-all")
    public ResponseEntity<?> clearStoredImages() {
        try {
            service.clearStoredImages();
            return ResponseEntity.ok("cleared");
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllImages() {
        return  ResponseEntity.ok(service.findAll());
    }


}