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
import org.springframework.transaction.annotation.Transactional;
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
    public ResponseDto registerUser(SignupRequestDto dto){
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setStatus(true);
        result.setMsg("회원가입 성공!");
        if(!dto.getPassword().equals(dto.getPasswordCheck())){
            System.out.println(dto.getPassword());
            System.out.println(dto.getPasswordCheck());
            result.setStatus(false);
            result.setMsg("비밀번호와 비밀번호 확인은 같아야합니다.");
            return result;
        }
        User ispresent=userRepository.findByEmail(dto.getEmail()).orElse(null);
        if(ispresent!=null){
            result.setStatus(false);
            result.setMsg("이미 가입한 이메일 입니다.");
            return result;
        }
        User user=new User();
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        System.out.println(user.getEmail());
        userRepository.save(user);

        return result;
    }
    public ResponseDto initialRegister(MultipartFile multipartFile, InitialDto dto,
                                UserDetailsImpl userDetails)throws IOException{
        InitialInfo tmp=initialInfoRepository.findByUserId(userDetails.getId()).orElse(null);
        InitialInfo info=new InitialInfo();
        ResponseDto result = new ResponseDto();
        result.setHttp(200);
        result.setMsg("등록 성공!");
        result.setStatus(true);
        try {
            if(tmp==null) {
                info.setAge(dto.getAge());
                info.setFood(dto.getFood());
                info.setGender(dto.getGender());
                info.setSns_url(dto.getSns());
                info.setIntro(dto.getIntro());
                info.setLocation(dto.getLocation());
                info.setProfile_img(s3Uploader.upload(multipartFile));
                long idnum = userDetails.getId();
                info.setUserId(idnum);
                initialInfoRepository.save(info);
            }
            else{
                result.setMsg("이미 등록된 사용자입니다.");
                result.setStatus(false);
            }
        }
        catch(Exception e) {
            result.setHttp(200);
            result.setMsg("등록 실패...");
            result.setStatus(false);
        }
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
        result.setIntro(info.getIntro());
        result.setLocation(info.getLocation());
        return result;
    }


    public ResponseDto modifyinitial(MultipartFile multipartFile, InitialDto dto, long id) throws IOException {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setMsg("수정 성공!");
        result.setStatus(true);
        try {
            info.setGender(dto.getGender());
            info.setFood(dto.getFood());
            info.setSns_url(dto.getSns());
            info.setAge(dto.getAge());
            info.setProfile_img(s3Uploader.upload(multipartFile));
            info.setLocation(dto.getLocation());
            info.setIntro(dto.getIntro());
            initialInfoRepository.save(info);
        }
        catch(Exception e){
            result.setHttp(200);
            result.setMsg("수정 실패...");
            result.setStatus(false);
        }

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

            user.setNickname(dto.getNickname());
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

            userRepository.save(user);

            result.setHttp(200);
            result.setMsg("수정 성공!");
            result.setStatus(true);

        }

        return result;
    }

    @Transactional
    public ResponseDto deleteuser(long id) {
        ResponseDto result=new ResponseDto();
        try {

            initialInfoRepository.deleteInitialInfoByUserId(id);
            userRepository.deleteById(id);
        }
        catch(Exception e){
            result.setHttp(200);
            result.setMsg("삭제 실패...");
            result.setStatus(false);
            return result;
        }
        result.setHttp(200);
        result.setMsg("삭제 성공!");
        result.setStatus(true);
        return result;

    }
}
