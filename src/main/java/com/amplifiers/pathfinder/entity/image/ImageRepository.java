package com.amplifiers.pathfinder.entity.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query(value = "SELECT * FROM image WHERE filename=?", nativeQuery = true)
    public Image findByFilename(String filename);
}
