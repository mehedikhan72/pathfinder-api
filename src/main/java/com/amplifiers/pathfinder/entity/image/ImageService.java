package com.amplifiers.pathfinder.entity.image;

import com.amplifiers.pathfinder.cloudstorage.CloudStorageService;
import jakarta.activation.MimetypesFileTypeMap;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    private final Integer MAX_FILE_SIZE = 500 * 1024;

    public byte[] compressImage(MultipartFile file) throws Exception {
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

        System.out.println(finalImageData.length / (float)file.getBytes().length);

        ImageIO.write(compressedImage, "jpg", new File("test.jpg"));

        return finalImageData;
    }

    private boolean isValidImageFile(MultipartFile file) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        String allowedExts[] = {"jpg", "jpeg", "gif", "png", "bmp", "wbmp"};

        for (String allowedExt : allowedExts) {
            if (allowedExt.equals(ext)) {
                return true;
            }
        }

        return false;
    }
    public Image saveImage(MultipartFile file) throws Exception {
        if (!isValidImageFile(file)) {
            throw new Exception("Invalid image file. Please upload .jpg, .png, .gif or .bmp file.");
        }

        String fileName = ("pf-" + FilenameUtils.getBaseName(file.getOriginalFilename()) + "-" + UUID.randomUUID()).replaceAll("\\.", "");
        String keyName = fileName + "." + "jpg";

        byte[] fileData = compressImage(file);
        CloudStorageService.uploadFile(fileData, keyName);
        Image image = Image.builder().filename(fileName).format("jpg").url(keyName).build();

        return repository.save(image);
    }

    public List<Image> findAll() {
        return repository.findAll();
    }

    public Image getImage(String filename) {
        filename = FilenameUtils.getBaseName(filename);
        Image image = repository.findByFilename(filename);

        return image;
    }

    public void clearStoredImages() throws Exception{
        CloudStorageService.clearBucket("pathfinder-bucket");
        repository.deleteAll();
    }
}
