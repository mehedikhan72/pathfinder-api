package com.amplifiers.pathfinder.entity.gig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GigRepository extends JpaRepository<Gig, Integer> {
    Optional<Gig> findById(int id);
}
