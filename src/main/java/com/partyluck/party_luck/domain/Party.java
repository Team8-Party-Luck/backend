package com.partyluck.party_luck.domain;

import javax.persistence.*;
import java.util.List;

@Entity
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

    @Column(nullable = false)
    private String location_big;

    @Column(nullable = false)
    private String location_small;

    @OneToMany(mappedBy = "party")
    private List<PartyJoin> partyJoinList;

    @OneToMany(mappedBy = "party")
    private List<Subscribe> subscribeList;
}
