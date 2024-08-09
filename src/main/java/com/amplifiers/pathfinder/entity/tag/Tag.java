package com.amplifiers.pathfinder.entity.tag;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;
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
    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private Set<Gig> gigs;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private Set<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Tag))
            return false;

        Tag other = (Tag) o;

        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
