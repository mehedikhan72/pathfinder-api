package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.entity.image.Image;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user", uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Integer id;

  @NotBlank(message = "First name is required.")
  private String firstname;

  @NotBlank(message = "Last name is required.")
  private String lastname;

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
  private Image profile_image;

//  @OneToMany(mappedBy = "user")
//  private List<Token> tokens;
//
//  @OneToMany(mappedBy = "user")
//  private List<Gig> gigs;
//
//  @OneToMany(mappedBy = "buyer")
//  private List<Enrollment> enrollments;

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
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
