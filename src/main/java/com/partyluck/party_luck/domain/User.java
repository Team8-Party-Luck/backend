package com.partyluck.party_luck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
    private String email;

//    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

//    @Column(nullable = false)
//    private String profileImg;

//    @Column(nullable = false)
//    @Enumerated(value = EnumType.STRING)
//    private UserRoleEnum role;

//    @OneToMany(mappedBy = "user")
//    private List<PartyJoin> partyJoinList;
//
//    @OneToMany(mappedBy = "user")
//    private List<Subscribe> subscribeList;

}
