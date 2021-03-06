package com.partyluck.party_luck.party.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    private List<String> age;
    private String gender;
    private String xy;
    private String place_url;
}