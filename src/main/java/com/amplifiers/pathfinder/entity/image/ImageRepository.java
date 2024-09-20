package com.amplifiers.pathfinder.entity.image;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Optional<Image> findByBasename(String basename);
}
