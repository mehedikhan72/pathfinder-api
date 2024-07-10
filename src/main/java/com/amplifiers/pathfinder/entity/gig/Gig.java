package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.faq.FAQ;
import com.amplifiers.pathfinder.entity.image.Image;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.video.Video;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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
    private Integer total_orders;
    private boolean accepted;

    // @JsonIgnore
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User seller;

    @NotNull(message = "At least one tag is required.")
    @NotEmpty(message = "At least one tag is required.")
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "gig_tag", joinColumns = @JoinColumn(name = "gig_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    // @OneToMany(mappedBy = "gig")
    // private Set<FAQ> faqs;

    // @OneToMany(mappedBy = "gig")
    // private Set<Enrollment> enrollments;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @OneToOne
    @JoinColumn(name = "gig_cover_image")
    private Image gig_cover_image;

    @OneToOne
    @JoinColumn(name = "gig_video")
    private Video gig_video;
}