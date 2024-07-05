package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.faq.FAQ;
import com.amplifiers.pathfinder.entity.tag.Tag;
import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    private String title;
    private String description;
    private String category;
    private float price;
    private float rating;
    private Integer total_orders;
    private boolean accepted;

    // @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "gig_tag",
            joinColumns = @JoinColumn(name = "gig_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

//    @OneToMany(mappedBy = "gig")
//    private Set<FAQ> faqs;

//    @OneToMany(mappedBy = "gig")
//    private Set<Enrollment> enrollments;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime created_at;
}
