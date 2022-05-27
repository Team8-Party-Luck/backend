package com.partyluck.party_luck.repository;

import com.partyluck.party_luck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByNickname(String nickname);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}
