package com.partyluck.party_luck.domain;

import com.partyluck.party_luck.dto.user.request.ReportRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reportId;

    private Long otherId;

    private String report;

    public Report(Long id, ReportRequestDto dto){
        this.reportId=id;
        this.otherId=dto.getOtherId();
        this.report=dto.getReport();
    }


}
