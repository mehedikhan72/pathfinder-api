package com.amplifiers.pathfinder.entity.tag;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)

public class Tag {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    @NotBlank(message = "Name is required.")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Gig> gigs;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<User> users;
}
