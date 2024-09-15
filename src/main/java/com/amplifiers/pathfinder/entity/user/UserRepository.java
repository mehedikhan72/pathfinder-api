package com.amplifiers.pathfinder.entity.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  Optional<User> findById(Integer id);
  Optional<User> findByEmailVerificationToken(String token);
}
