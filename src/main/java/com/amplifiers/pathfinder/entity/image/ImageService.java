package com.amplifiers.pathfinder.entity.image;

import com.amplifiers.pathfinder.cloudstorage.CloudStorageService;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    private final Integer MAX_FILE_SIZE = 500 * 1024;
    private final List<String> allowedExts = Arrays.asList("jpg", "jpeg", "gif", "png", "bmp", "wbmp");

    public byte[] compressImage(MultipartFile file) throws IOException {
        BufferedImage pngImg = ImageIO.read(file.getInputStream());
        BufferedImage img = new BufferedImage(
                pngImg.getWidth(),
                pngImg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(pngImg, 0, 0, Color.WHITE, null);


        // Resizing
//        System.out.println("Original Size : " + file.getBytes().length);
        BufferedImage compressedImage = Thumbnails.of(img)
                .size(2000, 2000)
                .keepAspectRatio(true)
                .asBufferedImage();

        ByteArrayOutputStream finalImageBAOS = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, "jpg", finalImageBAOS);
        byte[] finalImageData = finalImageBAOS.toByteArray();
//        System.out.println("Size after resizing : " + finalImageData.length);

        // Pass to reduce size
        int count = 1;
        while (finalImageData.length > MAX_FILE_SIZE) {
            compressedImage = Thumbnails.of(compressedImage)
                    .size(2000, 2000)
                    .keepAspectRatio(true)
                    .outputQuality(1 - .1 * count)
                    .asBufferedImage();
            ImageIO.write(compressedImage, "jpg", finalImageBAOS);

            finalImageData = finalImageBAOS.toByteArray();

            count++;

//            System.out.println("Size after pass " + count + " : " +finalImageData.length);
        }

//        System.out.println(finalImageData.length / (float)file.getBytes().length);

        ImageIO.write(compressedImage, "jpg", new File("test.jpg"));

        return finalImageData;
    }

    private boolean isValidImageFile(MultipartFile file) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

        for (String allowedExt : allowedExts) {
            if (allowedExt.equals(ext)) {
                return true;
            }
        }

        return false;
    }
    public Image saveImage(MultipartFile file) throws IOException {
        if (!isValidImageFile(file)) {
            throw new ValidationException("Invalid image file. Please upload .jpg, .png, .gif or .bmp file.");
        }

        String basename = ("pf-" + FilenameUtils.getBaseName(file.getOriginalFilename()) + "-" + UUID.randomUUID()).replaceAll("\\.", "");
        String keyName = basename + "." + "jpg";

        byte[] fileData = compressImage(file);

        CloudStorageService.uploadFile(fileData, keyName);
        Image image = Image.builder().basename(basename).created_at(LocalDateTime.now()).format("jpg").filename(keyName).build();

        System.out.println("Saved image : " + image.getFilename());

        return repository.save(image);
    }

    public List<Image> findAll() {
        return repository.findAll();
    }

    public Image getImage(String name) {
        String basename = FilenameUtils.getBaseName(name);
        return repository.findByBasename(basename)
                .orElseThrow(() -> new ResourceNotFoundException(basename + " does not exist."));
    }

    public void deleteImage(Integer id){
        Image image = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with id " + id + " doesn't exist"));

        CloudStorageService.deleteFile(image.getFilename());

        repository.deleteById(id);

        System.out.println("Deleted image : " + image.getFilename());
    }
}
