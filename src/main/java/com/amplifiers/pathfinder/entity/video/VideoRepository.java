package com.amplifiers.pathfinder.entity.video;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    Optional<Video> findByBasename(String basename);
}
