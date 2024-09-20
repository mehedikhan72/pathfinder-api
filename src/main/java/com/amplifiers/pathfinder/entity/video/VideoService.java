package com.amplifiers.pathfinder.entity.video;

import com.amplifiers.pathfinder.cloudstorage.CloudStorageService;
import com.amplifiers.pathfinder.cloudstorage.PresignedUrlInfo;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository repository;
    private final Integer maxFileSize = 100 * 1024 * 1024;
    private final List<String> allowedExts = Arrays.asList("mp4", "webm");

    public Video saveVideo(MultipartFile file) throws IOException {
        if (!isValidVideoFile(file)) {
            throw new ValidationException("Invalid video file. Please upload .mp4 or .webm file.");
        }

        if (file.getBytes().length > maxFileSize) {
            throw new ValidationException("Video file size can be max 100MB.");
        }

        String basename = ("pf-" + FilenameUtils.getBaseName(file.getOriginalFilename()) + "-" + UUID.randomUUID()).replaceAll("\\.", "");
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String keyName = basename + "." + ext;

        CloudStorageService.uploadFile(file.getBytes(), keyName);
        Video video = Video.builder().basename(basename).createdAt(OffsetDateTime.now()).format(ext).filename(keyName).build();

        System.out.println("Saved video : " + video.getFilename());

        return repository.save(video);
    }

    public List<Video> findAll() {
        return repository.findAll();
    }

    public void createVideoPresignedUrl(Video video) {
//        Video video = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Video with id " + id + " does not exist."));

        PresignedUrlInfo presignedUrlInfo = CloudStorageService.createPresignedGetUrl(video.getFilename());

        video.setPresignedUrl(presignedUrlInfo.getPresignedUrl().toExternalForm());
        video.setPresignedUrlExpire(presignedUrlInfo.getExpires());

        System.out.println("Generated new presigned url for video : " + video.getFilename());
        System.out.println(presignedUrlInfo.getPresignedUrl());

        repository.save(video);
    }
    public void deleteVideo(Integer id) {
        Video video = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video with id " + id + " doesn't exist"));

        CloudStorageService.deleteFile(video.getFilename());

        repository.deleteById(id);

        System.out.println("Deleted image : " + video.getFilename());
    }
    private boolean isValidVideoFile(MultipartFile file) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

        for (String allowedExt : allowedExts) {
            if (allowedExt.equals(ext)) {
                return true;
            }
        }

        return false;
    }
}
