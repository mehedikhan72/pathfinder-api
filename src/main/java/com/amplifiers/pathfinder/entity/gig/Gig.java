package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.video.Video;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "gig")
public class Gig {
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank(message = "Title is required.")
    private String title;
    @NotBlank(message = "Description is required.")
    private String description;
    @NotBlank(message = "Category is required.")
    private String category;
    @NotNull(message = "Price is required.")
    private float price;
    private float rating;
    private Integer totalOrders;
    private boolean accepted;

    // @JsonIgnore
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User seller;

//    @NotNull(message = "At least one tag is required.")
//    @NotEmpty(message = "At least one tag is required.")
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "gig_tag", joinColumns = @JoinColumn(name = "gigId"), inverseJoinColumns = @JoinColumn(name = "tagId"))
    private Set<Tag> tags;

    // @OneToMany(mappedBy = "gig")
    // private Set<FAQ> faqs;

    // @OneToMany(mappedBy = "gig")
    // private Set<Enrollment> enrollments;
//
//    @CreatedDate
//    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "gigCoverImage")
    private Image gigCoverImage;

    @OneToOne
    @JoinColumn(name = "gigVideo")
    private Video gigVideo;

    // a number of gigs will be featured every once in a while. there will be rolling substitution.
    // TODO: improve featured idea later.
//    private boolean featured;
}