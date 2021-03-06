package com.partyluck.party_luck.user.repository;

import com.partyluck.party_luck.user.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {
    Optional<Report> findByReportIdAndOtherId(Long reportId, Long otherId);
}
