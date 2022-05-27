package com.partyluck.party_luck.alarm;

import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteAllByUser(User user);

}
