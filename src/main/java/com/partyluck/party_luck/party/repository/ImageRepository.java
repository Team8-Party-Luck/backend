package com.partyluck.party_luck.party.repository;

import com.partyluck.party_luck.party.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findAllByPartyid(Long partyid);
    void deleteAllByPartyid(Long id);
    Optional<Image> findImageByImgIndexAndPartyid(Integer imgindex, Long id);
}