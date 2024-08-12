package com.amplifiers.pathfinder.entity.session;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

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
    private OffsetDateTime createdAt;

    @NotNull(message = "Scheduled date is required.")
    @Future(message = "Scheduled date must be in the future.")
    private OffsetDateTime scheduledAt;
    private OffsetDateTime completedAt;
    private boolean completed;

    // string for now, it will be online, offline and
    // stuff like that.
    @NotBlank(message = "Session type is required.")
    private String sessionType;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollmentId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Enrollment enrollment;

    private boolean buyerConfirmed;

    private boolean cancelled;

    @Enumerated(EnumType.STRING)
    private CancelledBy cancelledBy;
    private String cancellationReason;
    private OffsetDateTime cancelledAt;

    // TODO: more data about session will be added here, later.
}
