package com.partyluck.party_luck.user.repository;

import com.partyluck.party_luck.user.domain.InitialInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InitialInfoRepository extends JpaRepository<InitialInfo,Long> {
    Optional<InitialInfo> findByUserId(Long id);
    void deleteInitialInfoByUserId(Long id);
    Optional<InitialInfo> findInitialInfoByUserId(Long userId);

}