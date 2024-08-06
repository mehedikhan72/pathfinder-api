package com.amplifiers.pathfinder.entity.student_assessment;

import com.amplifiers.pathfinder.entity.session.Session;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "student_assessment")
public class StudentAssessment {
    @Id
    @GeneratedValue
    private Integer id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessionId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Session session;

    @NotNull(message = "Understanding rating is required.")
    private Integer understandingRating; // outtie 10

    @NotNull(message = "Response rating is required.")
    private Integer responseRating; // outtie 10

    @NotBlank(message = "Feedback is required.")
    private String feedback; // for the student
}
