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
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    private final Integer maxFileSize = 500 * 1024;
    private final List<String> allowedExts = Arrays.asList("jpg", "jpeg", "gif", "png", "bmp", "wbmp");
    private final Integer compressionDimension1 = 2000;
    private final Integer compressionDimension2 = 2000;
    private final Integer countInitializer = 1;
    private final float qualityReductionFactor = 0.1f;

    public byte[] compressImage(MultipartFile file) throws IOException {
        BufferedImage pngImg = ImageIO.read(file.getInputStream());
        BufferedImage img = new BufferedImage(
                pngImg.getWidth(),
                pngImg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(pngImg, 0, 0, Color.WHITE, null);

        // Resizing
        // System.out.println("Original Size : " + file.getBytes().length);
        BufferedImage compressedImage = Thumbnails.of(img)
                .size(compressionDimension1, compressionDimension2)
                .keepAspectRatio(true)
                .asBufferedImage();

        ByteArrayOutputStream finalImageBAOS = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, "jpg", finalImageBAOS);
        byte[] finalImageData = finalImageBAOS.toByteArray();
        // System.out.println("Size after resizing : " + finalImageData.length);

        // Pass to reduce size
        int count = countInitializer;
        while (finalImageData.length > maxFileSize) {
            compressedImage = Thumbnails.of(compressedImage)
                    .size(compressionDimension1, compressionDimension2)
                    .keepAspectRatio(true)
                    .outputQuality(1 - qualityReductionFactor * count)
                    .asBufferedImage();
            ImageIO.write(compressedImage, "jpg", finalImageBAOS);

            finalImageData = finalImageBAOS.toByteArray();

            count++;

            // System.out.println("Size after pass " + count + " : "
            // +finalImageData.length);
        }

        // System.out.println(finalImageData.length / (float)file.getBytes().length);

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

        String basename = ("pf-" + FilenameUtils.getBaseName(file.getOriginalFilename()) + "-" + UUID.randomUUID())
                .replaceAll("\\.", "");
        String keyName = basename + "." + "jpg";

        byte[] fileData = compressImage(file);

        CloudStorageService.uploadFile(fileData, keyName);
        Image image = Image.builder().basename(basename).createdAt(OffsetDateTime.now()).format("jpg").filename(keyName)
                .build();

        System.out.println("Saved image : " + image.getFilename());

        return repository.save(image);
    }

    public List<Image> findAll() {
        return repository.findAll();
    }

    public Image getImageByName(String name) {
        String basename = FilenameUtils.getBaseName(name);
        return repository.findByBasename(basename)
                .orElseThrow(() -> new ResourceNotFoundException(basename + " does not exist."));
    }

    public byte[] getImageDataById(Integer id) {
        Image image = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image does not exist."));
        return CloudStorageService.getFile(image.getFilename());
    }

    public void deleteImageById(Integer id) {
        Image image = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with id " + id + " doesn't exist"));

        CloudStorageService.deleteFile(image.getFilename());

        repository.deleteById(id);

        System.out.println("Deleted image : " + image.getFilename());
    }
}
