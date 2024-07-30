package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.entity.tag.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class UserProfileDTO {
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private Role role;

    private String profileImage;

    private Integer age;

    private String description;

    private Set<Tag> tags;

    private Float rating;

    private Integer ratedByCount;

    private Integer totalStudents;

    private Integer totalCompletedEnrollments;

    private List<Achievement> education;

    private List<Achievement> qualification;
}
