package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "gig")
public class Gig {
    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private String description;
    private float price;
    private float rating;
    private Integer total_orders;
    private boolean accepted;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime created_at;
}
