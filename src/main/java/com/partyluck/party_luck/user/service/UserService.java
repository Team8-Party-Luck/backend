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

    //?????? ????????????
    public ResponseDto registerUser(SignupRequestDto dto){
        ResponseDto result=new ResponseDto(true,200,"???????????? ??????!");
        if(!dto.getPassword().equals(dto.getPasswordCheck())){
            return new ResponseDto(false,400,"??????????????? ???????????? ????????? ??????????????????.");
        }
        User ispresent=userRepository.findByEmail(dto.getEmail()).orElse(null);
        if(ispresent!=null){
            return new ResponseDto(false,400,"?????? ????????? ????????? ?????????.");
        }
        User user=new User(passwordEncoder, dto);
        userRepository.save(user);
        return result;
    }
    //?????? ???????????? ??????
    public ResponseDto InitialRegister(InitialDto dto,
                                       UserDetailsImpl userDetails)throws IOException{
        InitialInfo tmp=initialInfoRepository.findByUserId(userDetails.getId()).orElse(null);
        InitialInfo info;
        ResponseDto result = new ResponseDto(true,200,"?????? ??????!");
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
            return new ResponseDto(false,500,"?????? ??????...");
        }
        return result;

    }

    //?????? ???????????? ??????
    public InitialResponseDto myInitial(long id) {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        InitialResponseDto result=new InitialResponseDto(info);
        result.setNickname(userRepository.findById(id).orElse(null).getNickname());
        return result;
    }

    //?????? ???????????? ??????
    public ResponseDto modifyInitial(InitialDto dto, long id) throws IOException {
        InitialInfo info=initialInfoRepository.findByUserId(id).orElse(null);
        ResponseDto result=new ResponseDto(true,200,"?????? ??????!");
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
            return new ResponseDto(false,400,"?????? ??????..");
        }
        return result;
    }
    //?????? ???????????? ??????
    public UserResponseDto userView(long id) {
        User user=userRepository.findById(id).orElse(null);
        UserResponseResultDto resultDto=new UserResponseResultDto(user,id);
        InitialInfo tmp=initialInfoRepository.findByUserId(id).orElse(null);
        if(tmp==null)
            return new UserResponseDto(false,resultDto);
        else
            return new UserResponseDto(true,resultDto);

    }
    //???????????? ??????
    public ResponseDto modifyUser(long id, ModifyUserRequestDto dto) {
        User user=userRepository.findById(id).orElse(null);
        ResponseDto result=new ResponseDto(false,400,ILLEGAL_PASSWORD_INVALIDATION);
        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            user.setNickname(dto.getNickname());
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            return new ResponseDto(true,200,"?????? ??????!");
        }
        return result;
    }
    //?????? ??????
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
            return new ResponseDto(false,500,"?????? ??????...");
        }
        return new ResponseDto(true,200,"?????? ??????!");
    }

    public ResponseDto reportUser(long id, ReportRequestDto dto) {
        Report tmp=reportRepository.findByReportIdAndOtherId(id,dto.getOtherId()).orElse(null);
        User user=userRepository.findById(dto.getOtherId()).orElse(null);
        if(tmp==null&&user!=null) {
            Report report = new Report(id, dto);
            reportRepository.save(report);
            return new ResponseDto(true,200,"????????? ?????????????????????");
        }
        else if(tmp == null)
            return new ResponseDto(false,404,"????????? ???????????? ????????????");
        else
            return new ResponseDto(false,400,"?????? ????????? ??????????????????");
    }
}