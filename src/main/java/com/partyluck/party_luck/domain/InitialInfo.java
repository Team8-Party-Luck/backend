package com.partyluck.party_luck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class InitialInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long initial_info_id;

    private String profile_img;

    private String food;

    private String age;

    private String gender;

    private String sns_url;

    private String region;

    private String city;

    private String intro;

    @Column(name="user_id")
    private Long userId;

}
