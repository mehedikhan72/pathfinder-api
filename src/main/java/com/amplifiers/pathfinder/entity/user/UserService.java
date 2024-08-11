package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.gig.GigCardDTO;
import com.amplifiers.pathfinder.entity.gig.GigService;
import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.image.ImageService;
import com.amplifiers.pathfinder.entity.review.ReviewCardDTO;
import com.amplifiers.pathfinder.entity.review.ReviewRepository;
import com.amplifiers.pathfinder.entity.review.ReviewService;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.tag.TagCreateRequest;
import com.amplifiers.pathfinder.entity.tag.TagService;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final ReviewRepository reviewRepository;
    private final GigService gigService;
    private final ReviewService reviewService;
    private final TagService tagService;
    private final EnrollmentRepository enrollmentRepository;
    private final ImageService imageService;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Wrong password. Please try again.");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new ValidationException("Password do not match. Please try again.");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    @Transactional
    public void editProfile(ProfileEditRequest request, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        System.out.println(request);

        if (request.getFirstName() != null) {
            if (request.getFirstName().isBlank()) {
                throw new ValidationException("First Name cannot be blank.");
            }
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            if (request.getLastName().isBlank()) {
                throw new ValidationException("Last Name cannot be blank.");
            }
            user.setLastName(request.getLastName());
        }

        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }

        if (request.getDescription() != null) {
            user.setDescription(request.getDescription());
        }

        if (request.getEducations() != null) {
            request.getEducations().forEach(e -> {
                if (e.title.isBlank())
                    throw new ValidationException("Education field cannot be blank");
            });
            user.setEducations(request.getEducations());
        }

        if (request.getQualifications() != null) {
            request.getQualifications().forEach(e -> {
                if (e.title.isBlank())
                    throw new ValidationException("Qualification field cannot be blank");
            });
            user.setQualifications(request.getQualifications());
        }

        if (request.getInterests() != null) {
            request.getInterests().forEach(
                    name -> {
                        System.out.println(tagService.findByName(name));
                        tagService.findByName(name).orElseGet(() -> tagService.createTag(new TagCreateRequest(name)));
                    });

            Set<Tag> tags = request.getInterests().stream()
                    .map(name -> tagService.findByName(name).get())
                    .collect(Collectors.toSet());

            user.setTags(tags);
        }

        repository.save(user);

        System.out.println(repository.findById(user.getId()).get());
    }

    public String setProfileImage(MultipartFile image, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        Image prevProfileImage = user.getProfileImage();
        Image profileImage;

        try {
            profileImage = imageService.saveImage(image);
        } catch (IOException E) {
            System.out.println("Failed to set profile image for " + user.getUsername());
            System.out.println(E.getMessage());
            throw new RuntimeException("Failed to set profile image for " + user.getUsername());
        }

        user.setProfileImage(profileImage);
        repository.save(user);

        if (prevProfileImage != null) {
            imageService.deleteImageById(prevProfileImage.getId());
        }

        return "Successfully " + (prevProfileImage != null ? "updated" : "set") + " the profile image of "
                + user.getUsername();
    }

    @Transactional
    public UserProfileDTO getUserProfileData(Integer id) {
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Profile not found."));

        Set<String> tags = user.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());

        List<Float> ratingList = reviewRepository.findAllRatingsBySellerId(id);
        Float rating = ratingList.size() > 0 ? (ratingList
                .stream()
                .reduce(Float.valueOf(0), (subtotal, gigRating) -> subtotal + gigRating)) / ratingList.size() : null;

        Integer ratedByCount = ratingList.size();

        Integer totalStudents = enrollmentRepository.countDistinctStudentsBySellerId(id);

        Integer totalCompletedEnrollments = enrollmentRepository.countCompletedBySellerId(id);

        return UserProfileDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .age(user.getAge())
                .description(user.getDescription())
                .tags(tags)
                .interests(tags)
                .rating(rating)
                .ratedByCount(ratedByCount)
                .totalStudents(totalStudents)
                .totalCompletedEnrollments(totalCompletedEnrollments)
                .educations(user.getEducations())
                .qualifications(user.getQualifications())
                .build();
    }

    public byte[] getProfileImageDataByUserId(Integer id) {
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return imageService.getImageDataById(user.getProfileImage().getId());
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public List<Gig> getGigs(Integer id) {
        User seller = repository.getReferenceById(id);

        return gigService.getGigsBySeller(seller);
    }

    public List<GigCardDTO> getGigCards(Integer id) {
        User seller = repository.getReferenceById(id);

        return gigService.getGigCardsBySeller(seller);
    }

    public List<ReviewCardDTO> getReviewCards(Integer id) {
        return reviewService.getReviewCardsBySellerId(id);
    }

    public User findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
