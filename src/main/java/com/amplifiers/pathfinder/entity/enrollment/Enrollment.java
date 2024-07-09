package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue
    private Integer id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gig_id")
    private Gig gig;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User buyer;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime created_at;
    private LocalDateTime deadline;
    private LocalDateTime completed_at;
    private float price;
    private Integer num_sessions;
    private Integer num_sessions_completed;
    private Integer session_duration_in_minutes;
    private boolean buyer_confirmed; // when buyer accepts the offer
    private boolean paid;
}
