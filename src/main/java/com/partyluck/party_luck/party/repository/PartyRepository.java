package com.partyluck.party_luck.party.repository;

import com.partyluck.party_luck.party.domain.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party,Long> {
    void deleteAllByUserid(Long id);
    List<Party> findAllByUserid(Long id);
}
