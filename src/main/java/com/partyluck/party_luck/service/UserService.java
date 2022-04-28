package com.partyluck.party_luck.service;

import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.domain.InitialInfo;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.repository.InitialInfoRepository;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final InitialInfoRepository initialInfoRepository;
    private final S3Uploader s3Uploader;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, InitialInfoRepository initialInfoRepository, S3Uploader s3Uploader) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.initialInfoRepository = initialInfoRepository;
        this.s3Uploader = s3Uploader;
    }
    public void registerUser(SignupRequestDto dto){
        User user=new User();
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        System.out.println(user.getEmail());
        userRepository.save(user);
    }
    public ResponseDto initialRegister(MultipartFile multipartFile, InitialDto dto,
                                UserDetailsImpl userDetails)throws IOException{
        InitialInfo info=new InitialInfo();
        info.setAge(dto.getAge());
        info.setFood(dto.getFood());
        info.setGender(dto.getGender());
        info.setSns_url(dto.getSns());
        info.setProfile_img(s3Uploader.upload(multipartFile));
        long idnum=userDetails.getId();
        info.setUserId(idnum);
        initialInfoRepository.save(info);
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setMsg("등록 성공!");
        result.setStatus(true);

        return result;

    }


    public InitialResponseDto myinitial(long id) {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        InitialResponseDto result=new InitialResponseDto();
        result.setAge(info.getAge());
        result.setFood(info.getFood());
        result.setGender(info.getGender());
        result.setImage(info.getProfile_img());
        result.setSns(info.getSns_url());
        return result;
    }


    public ResponseDto modifyinitial(MultipartFile multipartFile, InitialDto dto, long id) throws IOException {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        info.setGender(dto.getGender());
        info.setFood(dto.getFood());
        info.setSns_url(dto.getSns());
        info.setAge(dto.getAge());
        info.setProfile_img(s3Uploader.upload(multipartFile));
        initialInfoRepository.save(info);
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setMsg("수정 성공!");
        result.setStatus(true);

        return result;


    }

    public UserResponseDto userview(long id) {
        User user=userRepository.findById(id).orElse(null);
        UserResponseDto dto=new UserResponseDto();
        UserResponseResultDto resultDto=new UserResponseResultDto();
        dto.setOk(true);
        resultDto.setEmail(user.getEmail());
        resultDto.setPassword(user.getPassword());
        resultDto.setUserid(id);
        resultDto.setNickname(user.getNickname());
        dto.setResult(resultDto);
        return dto;

    }

    public ResponseDto modifyuser(long id, ModifyUserRequestDto dto) {
        User user=userRepository.findById(id).orElse(null);
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setMsg("수정 실패...");
        result.setStatus(false);
        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            user.setEmail(dto.getEmail());
            user.setNickname(dto.getNickname());
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            user.setUsername(dto.getEmail());
            userRepository.save(user);

            result.setHttp(200);
            result.setMsg("수정 성공!");
            result.setStatus(true);

        }

        return result;
    }
}
