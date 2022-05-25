package com.partyluck.party_luck.dto.user.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InitialDto {
    private MultipartFile image;
    List<String> food;
    private String age;
    private String gender;
    private String sns;
    private String intro;
    private String nickname;
    private String city;
    private String region;

}
