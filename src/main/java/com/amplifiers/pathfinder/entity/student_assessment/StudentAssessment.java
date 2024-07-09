package com.amplifiers.pathfinder.entity.student_assessment;

import com.amplifiers.pathfinder.entity.session.Session;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @JoinColumn(name = "session_id")
    private Session session;

    private Integer understanding_rating; // outtie 10
    private Integer response_rating; // outtie 10

    private String feedback; // for the student
}
