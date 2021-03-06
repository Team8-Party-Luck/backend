package com.partyluck.party_luck.user.service;

import com.partyluck.party_luck.chatroom.repository.JoinChatRoomRepository;
import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.party.domain.Party;
import com.partyluck.party_luck.party.repository.ImageRepository;
import com.partyluck.party_luck.party.repository.PartyRepository;
import com.partyluck.party_luck.party.repository.SubscribeRepository;
import com.partyluck.party_luck.party.responseDto.ResponseDto;
import com.partyluck.party_luck.user.domain.InitialInfo;
import com.partyluck.party_luck.user.domain.Report;
import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.user.requestDto.InitialDto;
import com.partyluck.party_luck.user.requestDto.ModifyUserRequestDto;
import com.partyluck.party_luck.user.requestDto.ReportRequestDto;
import com.partyluck.party_luck.user.requestDto.SignupRequestDto;
import com.partyluck.party_luck.user.responseDto.InitialResponseDto;
import com.partyluck.party_luck.user.responseDto.UserResponseDto;
import com.partyluck.party_luck.user.responseDto.UserResponseResultDto;
import com.partyluck.party_luck.party.repository.PartyJoinRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.user.repository.InitialInfoRepository;
import com.partyluck.party_luck.user.repository.ReportRepository;
import com.partyluck.party_luck.user.repository.UserRepository;
import com.partyluck.party_luck.alarm.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.partyluck.party_luck.exception.ExceptionMessage.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final InitialInfoRepository initialInfoRepository;
    private final S3Uploader s3Uploader;
    private final PartyJoinRepository partyJoinRepository;
    private final AlarmRepository alarmRepository;
    private final ReportRepository reportRepository;
    private final SubscribeRepository subscribeRepository;
    private final JoinChatRoomRepository joinChatRoomRepository;
    private final PartyRepository partyRepository;
    private final ImageRepository imageRepository;

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
//            info.setGender(dto.getGender());
            String s="";
            try {
                for (int i = 0; i < dto.getFood().size(); i++)
                    s = s + dto.getFood().get(i) + " ";
                info.setFood(s.substring(0, s.length() - 1));
            }catch (Exception e){
                info.setFood("");
            }
            info.setSns_url(dto.getSns());
//            info.setAge(dto.getAge());
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
            alarmRepository.deleteAllByUser(userRepository.findById(id).orElse(null));
            partyJoinRepository.deleteAllByUser(userRepository.findById(id).orElse(null));
            subscribeRepository.deleteAllByUser(userRepository.findById(id).orElse(null));
            List<Party> tmp=partyRepository.findAllByUserid(id);
            for(Party p:tmp){
                imageRepository.deleteAllByPartyid(p.getId());
                subscribeRepository.deleteAllByParty(p);
                partyJoinRepository.deleteAllByParty(p);
            }
            partyRepository.deleteAllByUserid(id);
            joinChatRoomRepository.deleteAllByUser(userRepository.findById(id).orElse(null));
            initialInfoRepository.deleteInitialInfoByUserId(id);
            userRepository.deleteById(id);
        }
        catch(Exception e){
            return new ResponseDto(false,500,"삭제 실패...");
        }
        return new ResponseDto(true,200,"삭제 성공!");
    }

    public ResponseDto reportUser(long id, ReportRequestDto dto) {
        Report tmp=reportRepository.findByReportIdAndOtherId(id,dto.getOtherId()).orElse(null);
        User user=userRepository.findById(dto.getOtherId()).orElse(null);
        if(tmp==null&&user!=null) {
            Report report = new Report(id, dto);
            reportRepository.save(report);
            return new ResponseDto(true,200,"신고가 완료되었습니다");
        }
        else if(tmp == null)
            return new ResponseDto(false,404,"요청이 올바르지 않습니다");
        else
            return new ResponseDto(false,400,"이미 신고한 사용자입니다");
    }
}