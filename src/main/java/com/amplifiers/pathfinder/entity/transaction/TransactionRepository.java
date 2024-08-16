package com.amplifiers.pathfinder.entity.transaction;

import com.amplifiers.pathfinder.entity.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByTranxId(String tranxId);
}
