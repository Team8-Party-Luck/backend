package com.partyluck.party_luck.repository;

import com.partyluck.party_luck.domain.InitialInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InitialInfoRepository extends JpaRepository<InitialInfo,Long> {
    Optional<InitialInfo> findByUserId(Long id);
    void deleteInitialInfoByUserId(Long id);

}
