package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.image.ImageService;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.tag.TagCreateRequest;
import com.amplifiers.pathfinder.entity.tag.TagService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.video.Video;
import com.amplifiers.pathfinder.entity.video.VideoService;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                .totalOrders(0)
                .accepted(false)
                .seller(user)
                .tags(tags)
                .createdAt(LocalDateTime.now())
                .build();
        return repository.save(gig);
    }

    private Boolean isGigOfUser(Gig gig) {
        User user = userUtility.getCurrentUser();

        return Objects.equals(user.getId(), gig.getSeller().getId());
    }

    public List<Gig> findAll() {
        return repository.findAll();
    }

    public Gig findById(Integer id) {
        Gig gig = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        Video video = gig.getGigVideo();

        if (video != null) {
            if (video.getPresignedUrl() == null || video.getPresignedUrlExpire() == null || LocalDateTime.now().isAfter(video.getPresignedUrlExpire())) {
                videoService.createVideoPresignedUrl(video);
            }
        }

        return gig;
    }

    public String deleteGig(Integer gigId) {
        Gig gig = repository.findById(gigId)
                .orElseThrow(() -> new ResourceNotFoundException("Gig not found"));

        User user = userUtility.getCurrentUser();

        if (user.getId() != gig.getSeller().getId()) {
            throw new ValidationException("Only the owner of the gig can delete it.");
        }

        imageService.deleteImage(gig.getGigCoverImage().getId());
        videoService.deleteVideo(gig.getGigVideo().getId());

        // INFO: because cascading delete wasn't working for many to many for some reason.
        jdbcTemplate.update("DELETE FROM gig_tag WHERE gigId = ?", gigId);
        repository.deleteById(gigId);

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

        Image prevCoverImage = gig.getGigCoverImage();

        Image coverImage = imageService.saveImage(image);

        gig.setGigCoverImage(coverImage);
        repository.save(gig);

        if (prevCoverImage != null) {
            imageService.deleteImageById(prevCoverImage.getId());
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

        Video prevGigVideo = gig.getGigVideo();

        Video gigVideo = videoService.saveVideo(video);

        gig.setGigVideo(gigVideo);
        repository.save(gig);

        if (prevGigVideo != null) {
            videoService.deleteVideo(prevGigVideo.getId());
        }

        return gigVideo;
    }

    public Image getCoverImage(int id) {
        Gig gig = repository.getReferenceById(id);

        return gig.getGigCoverImage();
    }

    // INFO: HOW SEARCH WORKS.
    // when a user starts typing the query, we recommend them the tags and categories with the 'query' prefix.
    // If a user clicks on any of those categories or tags, we show em the gigs for that category or tag.
    // If a user doesn't click any of the tags/categories in the suggestion(under the search box), and clicks
    // enter after typing the search term, we show them the relevant gigs for the search term. i.e. the gigs where
    // the title/description/category/tag contain the search term.

    // we will get the results based on scores of the gigs with the touch of a bit of randomness so people can
    // explore unpopular gigs often. i.e. 20% randomness. // TODO: improve this fetching algorithm.

    // TODO: implement a scoring algorithm.
    // TODO: update all gig fetch functions to incorporate scoring.

//    public List<Gig> getSimilarGigs() {
//        // TODO
//    }

//    public List getCategoryAndTagsForKeyword() {
//        // TODO: return whether it's a category or tag. so we can call the corresponding function.
//        // ie. findByCategory() or findByQuery().
//    }

    public List<Gig> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Gig> findByQuery(String query) {
        return repository.findByQuery(query);
    }

//    public List<Gig> findByTag(String tag) {
//        // TODO
//    }

//    public List<Gig> getFeaturedGigs() {
//        // TODO
//    }
//
//    // For explore new talents feature.
//    public List<Gig> getNewGigs() {
//        // TODO
//    }
}
