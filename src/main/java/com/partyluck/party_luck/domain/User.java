package com.partyluck.party_luck.domain;

import com.partyluck.party_luck.dto.user.request.SignupRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

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

    public User(PasswordEncoder passwordEncoder, SignupRequestDto dto){
        this.email=dto.getEmail();
        this.password=passwordEncoder.encode(dto.getPassword());
        this.nickname=dto.getNickname();
        this.username=dto.getEmail();
    }

}
