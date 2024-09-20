package com.amplifiers.pathfinder.entity.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileEditRequest {
    private String firstName;
    private String lastName;
    private String description;
    private Integer age;
    private List<Achievement> educations;
    private List<Achievement> qualifications;
    private List<String> interests;
}
