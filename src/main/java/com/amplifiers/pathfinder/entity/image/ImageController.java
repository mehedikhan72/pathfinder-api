package com.amplifiers.pathfinder.entity.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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
    public ResponseEntity<?> findImage(@PathVariable("filename") String filename) {
        return service.getImage(filename);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllImages() {
        return  ResponseEntity.ok(service.findAll());
    }


}
