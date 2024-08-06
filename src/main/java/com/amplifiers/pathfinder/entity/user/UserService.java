package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.image.ImageService;
import com.amplifiers.pathfinder.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
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

        Image prevProfileImage = user.getProfile_image();
        Image profileImage;

        try {
            profileImage = imageService.saveImage(image);
        } catch (IOException E) {
            System.out.println("Failed to set profile image for " + user.getUsername());
            System.out.println(E.getMessage());
            throw new RuntimeException("Failed to set profile image for " + user.getUsername());
        }

        user.setProfile_image(profileImage);
        repository.save(user);

        if (prevProfileImage != null) {
            imageService.deleteImageById(prevProfileImage.getId());
        }

        return "Successfully " + (prevProfileImage != null ? "updated" : "set") + " the profile image of " + user.getUsername();
    }

    public List<User> findAll() {
        return repository.findAll();
    }
}
