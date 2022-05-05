package com.partyluck.party_luck.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PartyModifyDto {
    private MultipartFile[] image;
    private Integer[] imageIndex;
    private String title;
    private Integer capacity;
    private String address;
    private String store;
    private String date;
    private String time;
    private String meeting;
    private String desc;
}
