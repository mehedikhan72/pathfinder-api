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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;
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
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Category is required.")
    private String category;

    @NotNull(message = "Price is required.")
    private Float price;

    @NotBlank(message = "Offer text is required.")
    private String offerText;

    @Formula(
            """
                    (select avg(r.rating) from review r where r.gig_id = id)
                    """
    )
    @Basic(fetch = FetchType.LAZY)
    private Float rating;

    @Formula(
            """
                    (select count(*) from enrollment e where e.gig_id = id and e.paid)
                    """
    )
    //    @Basic(fetch = FetchType.LAZY)
    private Integer totalOrders;

    private boolean accepted;
    private boolean paused;

    // @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User seller;

    //    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "gig_tag", joinColumns = @JoinColumn(name = "gigId"), inverseJoinColumns = @JoinColumn(name = "tagId"))
    private Set<Tag> tags;

    @JsonIgnore
    @ElementCollection(targetClass = FAQ.class)
    @CollectionTable(name = "faqs", joinColumns = @JoinColumn(name = "gigId"))
    @Column(name = "faqs")
    private List<FAQ> faqs;

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

    private Integer score = 0;
}
