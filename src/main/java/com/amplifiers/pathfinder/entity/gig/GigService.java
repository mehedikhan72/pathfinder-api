package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.tag.TagCreateRequest;
import com.amplifiers.pathfinder.entity.tag.TagRepository;
import com.amplifiers.pathfinder.entity.tag.TagService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GigService {
    private final GigRepository repository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public Gig createGig(GigCreateRequest request) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        // Fetch user from repository using userEmail
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        request.getTags().forEach(name -> tagService.findByName(name).orElseGet(() -> tagService.createTag(new TagCreateRequest(name))));

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
                .user(user)
                .tags(tags)
                .created_at(LocalDateTime.now())
                .build();
        return repository.save(gig);
    }

    public List<Gig> findAll() {
        return repository.findAll();
    }

    public Gig findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gig not found"));
    }
}
