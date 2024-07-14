package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.image.ImageService;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.tag.TagCreateRequest;
import com.amplifiers.pathfinder.entity.tag.TagService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.entity.video.Video;
import com.amplifiers.pathfinder.entity.video.VideoService;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GigService {
    private final GigRepository repository;
    private final TagService tagService;
    private final UserUtility userUtility;
    private final ImageService imageService;
    private final VideoService videoService;
    private final JdbcTemplate jdbcTemplate;

    public Gig createGig(GigCreateRequest request) {
        User user = userUtility.getCurrentUser();
        request.getTags().forEach(
                name -> tagService.findByName(name).orElseGet(() -> tagService.createTag(new TagCreateRequest(name))));

        Set<Tag> tags = request.getTags().stream()
                .map(name -> tagService.findByName(name).get())
                .collect(Collectors.toSet());

        var gig = Gig.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .rating(0.0f)
                .total_orders(0)
                .accepted(false)
                .seller(user)
                .tags(tags)
                .created_at(LocalDateTime.now())
                .build();
        return repository.save(gig);
    }

    private Boolean isGigOfUser(Gig gig) {
        User user = userUtility.getCurrentUser();

        if (user.getId() == gig.getSeller().getId()) {
            return true;
        }

        return false;
    }

    public List<Gig> findAll() {
        return repository.findAll();
    }

    public Gig findById(Integer id) {
        Gig gig = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        Video video = gig.getGig_video();

        if (video != null) {
            if (video.getPresignedUrl() == null || video.getPresignedUrlExpire() == null || LocalDateTime.now().isAfter(video.getPresignedUrlExpire())) {
                videoService.createVideoPresignedUrl(video);
            }
        }

        return gig;
    }

    public List<Gig> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Gig> findByQuery(String query) {
        return repository.findByQuery(query);
    }

    public String deleteGig(Integer gig_id) {
        Gig gig = repository.findById(gig_id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        User user = userUtility.getCurrentUser();

        if (user.getId() != gig.getSeller().getId()) {
            throw new ValidationException("Only the owner of the gig can delete it.");
        }

        // INFO: because cascading delete wasn't working for many to many for some reason.
        jdbcTemplate.update("DELETE FROM gig_tag WHERE gig_id = ?", gig_id);
        repository.deleteById(gig_id);

        return "deleted";
    }

    public Image setCoverImage(Integer gigId, MultipartFile image) throws Exception {
        Gig gig = repository.getReferenceById(gigId);

        if (gig == null) {
            throw new ResourceNotFoundException("Gig id " + gigId + " does not exist.");
        }

        if (!isGigOfUser(gig)) {
            throw new ValidationException("User not owner of this gig");
        }

        Image prevCoverImage = gig.getGig_cover_image();

        Image coverImage = imageService.saveImage(image);

        gig.setGig_cover_image(coverImage);
        repository.save(gig);

        if (prevCoverImage != null) {
            imageService.deleteImage(prevCoverImage.getId());
        }

        return coverImage;
    }

    public Video setGigVideo(Integer gigId, MultipartFile video) throws Exception {
        Gig gig = repository.getReferenceById(gigId);

        if (gig == null) {
            throw new ResourceNotFoundException("Gig id " + gigId + " does not exist.");
        }

        if (!isGigOfUser(gig)) {
            throw new ValidationException("User not owner of this gig");
        }

        Video prevGigVideo = gig.getGig_video();

        Video gigVideo = videoService.saveVideo(video);

        gig.setGig_video(gigVideo);
        repository.save(gig);

        if (prevGigVideo != null) {
            videoService.deleteVideo(prevGigVideo.getId());
        }

        return gigVideo;
    }

    public Image getCoverImage(int id) {
        Gig gig = repository.getReferenceById(id);

        return gig.getGig_cover_image();
    }
}
