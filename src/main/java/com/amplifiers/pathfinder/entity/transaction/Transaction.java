package com.amplifiers.pathfinder.entity.transaction;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private Integer id;

    private String tranxId;

    // many to one since one enrollment can have many transactions. i.e. failed/incomplete transactions.
    // only one will be successful. the last one.
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollmentId")
    private Enrollment enrollment;
    private OffsetDateTime paidAt;
    private float amount;
    private boolean paymentConfirmed;
}
