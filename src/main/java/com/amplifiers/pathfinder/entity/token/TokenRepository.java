package com.amplifiers.pathfinder.entity.token;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {

  @Query("""
      select t from Token t inner join User u
      on t.user.id = u.id
      where u.id = :id and t.tokenType = 'ACCESS' and t.revoked = false
      """)
  List<Token> findAllValidAccessTokenByUser(Integer id);

  Optional<Token> findByToken(String token);

  void deleteByToken(String token);
}
