package com.partyluck.party_luck.domain;

import javax.persistence.*;

@Entity
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Party party;

}
