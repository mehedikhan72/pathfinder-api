package com.amplifiers.pathfinder.entity.faq;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "faq")
public class FAQ {
    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank(message = "Question is required.")
    private String question;

    @NotBlank(message = "Answer is required.")
    private String answer;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gigId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Gig gig;
}
