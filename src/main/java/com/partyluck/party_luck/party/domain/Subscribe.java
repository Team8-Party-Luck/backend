package com.partyluck.party_luck.party.domain;

import com.partyluck.party_luck.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Party party;

    public Subscribe(User user, Party party){
        this.user=user;
        this.party=party;
    }

}
