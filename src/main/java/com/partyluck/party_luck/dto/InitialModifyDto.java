package com.partyluck.party_luck.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class InitialModifyDto {
    private MultipartFile image;
    private String food;
    private String age;
    private String gender;
    private String sns;
    private String intro;
    private String nickname;
    private String city;
    private String region;

}
