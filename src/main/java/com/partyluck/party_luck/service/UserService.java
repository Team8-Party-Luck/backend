package com.partyluck.party_luck.service;

import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.dto.SignupRequestDto;
import com.partyluck.party_luck.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    public void registerUser(SignupRequestDto dto){
        User user=new User();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        System.out.println(user.getEmail());
        userRepository.save(user);
    }




}
