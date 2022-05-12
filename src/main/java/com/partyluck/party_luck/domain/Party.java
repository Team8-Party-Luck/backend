package com.partyluck.party_luck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String date;

    private String time;

    private String store;

    private String address;

    private String meeting;

    private String gender;

    private String age;

    private String place_url;

    private String xy;

    private Long userid;

    @OneToMany(mappedBy = "party")
    private List<PartyJoin> partyJoinList;

    @OneToMany(mappedBy = "party")
    private List<Subscribe> subscribeList;
}
