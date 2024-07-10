package com.amplifiers.pathfinder.entity.session;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue
    private Integer id;

    // INFO: Business Logic - once an enrollment is created,
    // sessions under that enrollment will take place.
    // the seller will schedule each session and the buyer will
    // accept it. This is to make sure that the buyer is available
    // at that time. More info incoming.

    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime created_at;

    @NotNull(message = "Scheduled date is required.")
    @Future(message = "Scheduled date must be in the future.")
    private LocalDateTime scheduled_at;
    private LocalDateTime completed_at;
    private boolean completed;

    // string for now, it will be online, offline and
    // stuff like that.
    @NotBlank(message = "Session type is required.")
    private String session_type;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    private boolean buyer_confirmed;

    private boolean cancelled;

    @Enumerated(EnumType.STRING)
    private CancelledBy cancelled_by;
    private String cancellation_reason;
    private LocalDateTime cancelled_at;

    // TODO: more data about session will be added here, later.
}
