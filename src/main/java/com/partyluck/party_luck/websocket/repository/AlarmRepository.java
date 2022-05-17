package com.partyluck.party_luck.websocket.repository;

import com.partyluck.party_luck.websocket.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
