package com.partyluck.party_luck.dto.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportRequestDto {
    private Long otherId;
    private String report;
}
