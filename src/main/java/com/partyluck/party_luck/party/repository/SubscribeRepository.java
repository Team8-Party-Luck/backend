package com.partyluck.party_luck.user.controller.party.repository;

import com.partyluck.party_luck.user.controller.party.domain.Party;
import com.partyluck.party_luck.user.controller.party.domain.Subscribe;
import com.partyluck.party_luck.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe,Long> {
    Optional<Subscribe> findByPartyAndUser(Party party, User user);
    void deleteSubscribeByPartyAndUser(Party party,User user);
    void deleteAllByParty(Party party);

}
