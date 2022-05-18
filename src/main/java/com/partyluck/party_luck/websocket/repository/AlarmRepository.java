package com.partyluck.party_luck.websocket.repository;

import com.partyluck.party_luck.websocket.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserIdOrderByCreatedAtDesc(Long userId);

}
