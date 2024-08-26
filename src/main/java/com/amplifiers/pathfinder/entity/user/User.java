package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email.")
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required.")
    private String password;

    @NotNull(message = "Role is required.")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    @JoinColumn(name = "profile_image")
    private Image profileImage;

    // @OneToMany(mappedBy = "user")
    // private List<Token> tokens;
    //
    // @OneToMany(mappedBy = "user")
    // private List<Gig> gigs;
    //
    // @OneToMany(mappedBy = "buyer")
    // private List<Enrollment> enrollments;

    //// Extra Profile Data
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_tag", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @JsonIgnore
    private Integer age;

    @JsonIgnore
    @Column(columnDefinition = "text")
    private String description;

    @JsonIgnore
    @ElementCollection(targetClass = Achievement.class)
    @CollectionTable(name = "educations", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "educations")
    private List<Achievement> educations;

    @JsonIgnore
    @ElementCollection(targetClass = Achievement.class)
    @CollectionTable(name = "qualifications", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "qualifications")
    private List<Achievement> qualifications;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
