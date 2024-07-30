package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.entity.enrollment.EnrollmentRepository;
import com.amplifiers.pathfinder.entity.gig.GigRepository;
import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.image.ImageService;
import com.amplifiers.pathfinder.entity.review.ReviewRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final GigRepository gigRepository;
    private final ReviewRepository reviewRepository;
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

        return "Successfully " + (prevProfileImage != null ? "updated" : "set") + " the profile image of " + user.getUsername();
    }

    public UserProfileDTO getUserProfileData(Integer id, HttpServletRequest request) {
        User user = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Profile not found."));

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(null).build().toUriString();

        List<Float> ratingList = reviewRepository.findAllRatingsBySellerId(id);
         Float rating = ratingList.size() > 0 ? (ratingList
                 .stream()
                 .reduce(Float.valueOf(0), (subtotal, gigRating) -> subtotal + gigRating)) / ratingList.size() : 0;

         Integer ratedByCount = ratingList.size();

         Integer totalStudents = enrollmentRepository.countDistinctStudentsBySellerId(id);

         Integer totalCompletedEnrollments = enrollmentRepository.countCompletedBySellerId(id);

        return UserProfileDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .profileImage(user.getProfileImage() != null ? baseUrl + "/api/v1/public/images/" + user.getProfileImage().getFilename() : null)
                .role(user.getRole())
                .age(user.getAge())
                .description(user.getDescription())
                .tags(user.getTags())
                .rating(rating)
                .ratedByCount(ratedByCount)
                .totalStudents(totalStudents)
                .totalCompletedEnrollments(totalCompletedEnrollments)
                .education(user.getEducations())
                .qualification(user.getQualifications())
                .build();
    }

    public byte[] getProfileImageDataByUserId(Integer id) {
        User user = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found."));

        return imageService.getImageDataById(user.getProfileImage().getId());
    }
    public List<User> findAll() {
        return repository.findAll();
    }
}
