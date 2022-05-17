package com.partyluck.party_luck.repository;

import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.domain.PartyJoin;
import com.partyluck.party_luck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyJoinRepository extends JpaRepository<PartyJoin,Long> {
    Optional<PartyJoin> deleteByPartyAndUser(Party party,User user);
    List<PartyJoin>findAllByParty(Party party);
    Optional<PartyJoin>findPartyJoinByPartyAndUser(Party party,User user);
    void deleteAllByParty(Party party);
    List<PartyJoin> findPartyJoinsByUser(User user);
}
