package com.partyluck.party_luck.repository;

import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.domain.PartyJoin;
import com.partyluck.party_luck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartyJoinRepository extends JpaRepository<PartyJoin,Long> {
    Optional<PartyJoin> findByPartyAndUser(Party party, User user);
    Optional<PartyJoin> deleteByPartyAndUser(Party party,User user);
    void deletePartyJoinByParty(Party party);
}
