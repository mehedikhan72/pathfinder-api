package com.amplifiers.pathfinder.entity.report;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.amplifiers.pathfinder.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue
    private Integer id;

    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reportedUserId")
    private User reportedUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollmentId")
    private Enrollment enrollment;

    private boolean resolved;
    private String resolvedBy;

    private OffsetDateTime createdAt;
    private OffsetDateTime resolvedAt;

    // TODO: add report types later but for now, as our platform is just
    // TODO: starting out - this would suffice.
}
