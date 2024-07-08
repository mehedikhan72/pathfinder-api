package com.amplifiers.pathfinder.entity.image;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {


    public Optional<Image> findByBasename(String basename);
}
