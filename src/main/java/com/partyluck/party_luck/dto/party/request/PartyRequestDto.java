package com.partyluck.party_luck.dto.party.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PartyRequestDto {
    private MultipartFile[] image;
    private String defaultImage;
    private String title;
    private Integer capacity;
    private String address;
    private String store;
    private String date;
    private String time;
    private String meeting;
    private String desc;
    private String[] age;
    private String gender;
    private String xy;
    private String place_url;
}
