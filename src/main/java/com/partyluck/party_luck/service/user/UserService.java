package com.partyluck.party_luck.service.user;

import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.domain.InitialInfo;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.dto.user.request.InitialDto;
import com.partyluck.party_luck.dto.user.request.ModifyUserRequestDto;
import com.partyluck.party_luck.dto.user.request.SignupRequestDto;
import com.partyluck.party_luck.dto.user.response.InitialResponseDto;
import com.partyluck.party_luck.dto.user.response.UserResponseDto;
import com.partyluck.party_luck.dto.user.response.UserResponseResultDto;
import com.partyluck.party_luck.repository.InitialInfoRepository;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.partyluck.party_luck.exception.ExceptionMessage.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final InitialInfoRepository initialInfoRepository;
    private final S3Uploader s3Uploader;

    //일반 회원가입
    public ResponseDto registerUser(SignupRequestDto dto){
        ResponseDto result=new ResponseDto(true,200,"회원가입 성공!");
        if(!dto.getPassword().equals(dto.getPasswordCheck())){
            return new ResponseDto(false,400,"비밀번호와 비밀번호 확인은 같아야합니다.");
        }
        User ispresent=userRepository.findByEmail(dto.getEmail()).orElse(null);
        if(ispresent!=null){
            return new ResponseDto(false,400,"이미 가입한 이메일 입니다.");
        }
        User user=new User(passwordEncoder, dto);
        userRepository.save(user);
        return result;
    }
    //유저 상세정보 등록
    public ResponseDto InitialRegister(InitialDto dto,
                                       UserDetailsImpl userDetails)throws IOException{
        InitialInfo tmp=initialInfoRepository.findByUserId(userDetails.getId()).orElse(null);
        InitialInfo info;
        ResponseDto result = new ResponseDto(true,200,"등록 성공!");
        try {
            if(tmp==null) {
                info=new InitialInfo(dto,userDetails);
                initialInfoRepository.save(info);
                User usernick=userRepository.findById(userDetails.getId()).orElse(null);
                usernick.setNickname(dto.getNickname());
                userRepository.save(usernick);
            }
            else{
                return new ResponseDto(false,400,ILLEGAL_INITIALINFO_DUPLICATE);
            }
        }
        catch(Exception e) {
            return new ResponseDto(false,500,"등록 실패...");
        }
        return result;

    }

//본인 상세정보 조회
    public InitialResponseDto myInitial(long id) {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        InitialResponseDto result=new InitialResponseDto(info);
        result.setNickname(userRepository.findById(id).orElse(null).getNickname());
        return result;
    }

//유저 상세정보 수정
    public ResponseDto modifyInitial(InitialDto dto, long id) throws IOException {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        ResponseDto result=new ResponseDto(true,200,"수정 성공!");
        try {
            info.setGender(dto.getGender());
            String s="";
            for(int i=0;i<dto.getFood().size();i++)
                s=s+dto.getFood().get(i)+" ";
            info.setFood(s.substring(0,s.length()-1));
            info.setSns_url(dto.getSns());
            info.setAge(dto.getAge());
            if((dto.getImage()!=null)&&(!dto.getImage().isEmpty())) {
                info.setProfile_img(s3Uploader.upload(dto.getImage()));
            }
            info.setRegion(dto.getRegion());
            info.setCity(dto.getCity());
            info.setIntro(dto.getIntro());
            initialInfoRepository.save(info);
            User usernick=userRepository.findById(id).orElse(null);
            usernick.setNickname(dto.getNickname());
            userRepository.save(usernick);
        }
        catch(Exception e){
            return new ResponseDto(false,400,"수정 실패..");
        }
        return result;
    }
//본인 기본정보 조회
    public UserResponseDto userView(long id) {
        User user=userRepository.findById(id).orElse(null);
        UserResponseResultDto resultDto=new UserResponseResultDto(user,id);
        InitialInfo tmp=initialInfoRepository.findByUserId(id).orElse(null);
        if(tmp==null)
            return new UserResponseDto(false,resultDto);
        else
            return new UserResponseDto(true,resultDto);

    }
//기본정보 수정
    public ResponseDto modifyUser(long id, ModifyUserRequestDto dto) {
        User user=userRepository.findById(id).orElse(null);
        ResponseDto result=new ResponseDto(false,400,ILLEGAL_PASSWORD_INVALIDATION);
        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            user.setNickname(dto.getNickname());
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            return new ResponseDto(true,200,"수정 성공!");
        }
        return result;
    }
//회원 탈퇴
    @Transactional
    public ResponseDto deleteUser(long id) {
        try {
            initialInfoRepository.deleteInitialInfoByUserId(id);
            userRepository.deleteById(id);
        }
        catch(Exception e){
            return new ResponseDto(false,500,"삭제 실패...");
        }
        return new ResponseDto(true,200,"삭제 성공!");
    }
}
