package com.partyluck.party_luck.service;

import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.domain.Image;
import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.domain.PartyJoin;
import com.partyluck.party_luck.domain.Subscribe;
import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PartyService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PartyRepository partyRepository;
    private final S3Uploader s3Uploader;
    private final PartyJoinRepository partyJoinRepository;
    private final SubscribeRepository subscribeRepository;
    private final InitialInfoRepository initialInfoRepository;

    @Autowired
    public PartyService(UserRepository userRepository, ImageRepository imageRepository, PartyRepository partyRepository, S3Uploader s3Uploader, PartyJoinRepository partyJoinRepository, SubscribeRepository subscribeRepository, InitialInfoRepository initialInfoRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.partyRepository = partyRepository;
        this.s3Uploader = s3Uploader;
        this.partyJoinRepository = partyJoinRepository;
        this.subscribeRepository = subscribeRepository;
        this.initialInfoRepository = initialInfoRepository;
    }

    public ResponseDto registerparty(PartyRequestDto dto, long id) throws IOException {
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setMsg("등록 성공!");
        result.setStatus(true);
        try {
            Party party = new Party();
            party.setTitle(dto.getTitle());
            party.setCapacity(dto.getCapacity());
            party.setDate(dto.getDate());
            party.setDescription(dto.getDesc());
            party.setAddress(dto.getAddress());
            party.setStore(dto.getStore());
            party.setMeeting(dto.getMeeting());
            party.setTime(dto.getTime());
            party.setUserid(id);
            long partyid = partyRepository.save(party).getId();
            if((dto.getImage())!=null&&(!dto.getImage()[0].isEmpty())) {
                int leng = dto.getImage().length;
                for (int i = 0; i < leng; i++) {
                    Image image = new Image();
                    image.setImageSrc(s3Uploader.upload(dto.getImage()[i]));
                    image.setPartyid(partyid);
                    image.setImgIndex(i + 1);
                    imageRepository.save(image);
                }
            }
            PartyJoin partyJoin=new PartyJoin();
            partyJoin.setParty(partyRepository.findById(partyid).orElse(null));
            partyJoin.setUser(userRepository.findById(id).orElse(null));
            partyJoinRepository.save(partyJoin);
        }
        catch(Exception e){
            result.setHttp(200);
            result.setMsg("등록 실패...");
            result.setStatus(false);
        }
        return result;


    }

    public PartyResponseDto partyview(long id) {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        List<PartyResponseResultDto> results=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setAddress(p.getAddress());
            dto.setTitle(p.getTitle());
            dto.setStore(p.getStore());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setDesc(p.getDescription());
            if(p.getUserid()==id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            Subscribe issubpresent=subscribeRepository.findByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            if(issubpresent==null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            List<Image> itmp=imageRepository.findAllByPartyid(p.getId());
            String[] ist=new String[itmp.size()];
            for(int i=0;i<itmp.size();i++){
                ist[i]=itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            resultss.add(dto);
            String city1=initialInfoRepository.findByUserId(id).orElse(null).getCity();
            String region1=initialInfoRepository.findByUserId(id).orElse(null).getRegion();
            if((city1+" "+region1).equals(p.getAddress()))
                results.add(dto);

        }
        partyResponseDto.setResults(results);
        return partyResponseDto;
    }
    @Transactional
    public ResponseDto deleteparty(Long id) {
        ResponseDto result=new ResponseDto();
        try{
            imageRepository.deleteAllByPartyid(id);
            partyJoinRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
            subscribeRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
            partyRepository.deleteById(id);
        }
        catch(Exception e){
            result.setHttp(200);
            result.setStatus(false);
            result.setMsg("삭제 실패...");
            return result;
        }
        result.setHttp(200);
        result.setStatus(true);
        result.setMsg("삭제 성공!");
        return result;
    }

    public String partyjoin(Long id, long id1) {
        String result="";
        PartyJoin tmp=partyJoinRepository.findPartyJoinByPartyAndUser(partyRepository.findById(id).orElse(null),userRepository.findById(id1).orElse(null)).orElse(null);
        try {
            if(tmp==null) {
                PartyJoin partyJoin = new PartyJoin();
                partyJoin.setParty(partyRepository.findById(id).orElse(null));
                partyJoin.setUser(userRepository.findById(id1).orElse(null));
                partyJoinRepository.save(partyJoin);
            }
            else
                return "이미 가입한 파티입니다";
        }
        catch(Exception e){
            result="참가 실패...";
            return result;
        }
        result="참가가 완료되었습니다.";
        return result;
    }

    @Transactional
    public String partyout(Long id, long id1) {
        try {
            partyJoinRepository.deleteByPartyAndUser(partyRepository.findById(id).orElse(null),userRepository.findById(id1).orElse(null));
        }
        catch(Exception e){
            return "탈퇴에 실패했습니다...";
        }
        return "탈퇴 성공!";
    }

    @Transactional
    public String likeparty(Long id, long id1) {

        Subscribe subscribe=subscribeRepository.findByPartyAndUser(partyRepository.findById(id).orElse(null),userRepository.findById(id1).orElse(null)).orElse(null);
        try {
            if (subscribe == null) {
                Subscribe tmp = new Subscribe();
                tmp.setParty(partyRepository.findById(id).orElse(null));
                tmp.setUser(userRepository.findById(id1).orElse(null));
                subscribeRepository.save(tmp);
            } else {
                subscribeRepository.deleteSubscribeByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null));

            }
        }
        catch(Exception e){
            return "오류가 발생했습니다...";
        }
        return "성공!";
    }

    public PartyDetailsResponseDto partydetail(Long id, long id1) {
        PartyDetailsResponseDto result=new PartyDetailsResponseDto();
        Party party=partyRepository.findById(id).orElse(null);
        result.setCapacity(party.getCapacity());
        result.setDate(party.getDate());
        result.setDesc(party.getDescription());
        result.setPartyid(id);
        result.setStore(party.getStore());
        result.setAddress(party.getAddress());
        result.setHostid(userRepository.findById(party.getUserid()).orElse(null).getId());
        result.setHost(userRepository.findById(party.getUserid()).orElse(null).getNickname());
        result.setTitle(party.getTitle());
        result.setMeeting(party.getMeeting());
        result.setTime(party.getTime());
        result.setMemberCnt(partyJoinRepository.findAllByParty(party).size());
        List<Image> itmp=imageRepository.findAllByPartyid(id);
        String[] ist=new String[itmp.size()];
        for(int i=0;i< itmp.size();i++)
            ist[i]=itmp.get(i).getImageSrc();
        result.setImage(ist);
        Subscribe subscribe=subscribeRepository.findByPartyAndUser(party,userRepository.findById(id1).orElse(null)).orElse(null);
        if(subscribe==null)
            result.setSub(false);
        else
            result.setSub(true);
        PartyJoin partyJoin=partyJoinRepository.findPartyJoinByPartyAndUser(party,userRepository.findById(id1).orElse(null)).orElse(null);
        if(partyJoin==null)
            result.setJoin(false);
        else
            result.setJoin(true);
        return result;


    }

    public PartyResponseDto mysubparty(long id) {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        List<PartyResponseResultDto> results=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setAddress(p.getAddress());
            dto.setDesc(p.getDescription());
            List<Image> itmp=imageRepository.findAllByPartyid(p.getId());
            String[] ist=new String[itmp.size()];
            for(int i=0;i<itmp.size();i++){
                ist[i]=itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            if(p.getUserid()==id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            dto.setIssub(false);
            resultss.add(dto);
            Subscribe subscribe=subscribeRepository.findByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            if(subscribe!=null) {
                dto.setIssub(true);
                results.add(dto);
            }

        }
        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    public PartyResponseDto myhostparty(long id) {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        List<PartyResponseResultDto> results=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setAddress(p.getAddress());
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            List<Image> itmp=imageRepository.findAllByPartyid(p.getId());
            String[] ist=new String[itmp.size()];
            for(int i=0;i<itmp.size();i++){
                ist[i]=itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            dto.setIshost(false);
            Subscribe issubpresent=subscribeRepository.findByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            if(issubpresent==null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            resultss.add(dto);
            if(p.getUserid()==id) {
                dto.setIshost(true);
                results.add(dto);
            }
        }
        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    public PartyResponseDto willjoinparty(long id) {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        List<PartyResponseResultDto> results=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setAddress(p.getAddress());
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            List<Image> itmp=imageRepository.findAllByPartyid(p.getId());
            String[] ist=new String[itmp.size()];
            for(int i=0;i<itmp.size();i++){
                ist[i]=itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            Subscribe issubpresent=subscribeRepository.findByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            if(issubpresent==null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            if(p.getUserid()==id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            resultss.add(dto);
            PartyJoin partyJoin=partyJoinRepository.findPartyJoinByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1=p.getDate().split("월 ");//tmp1.[0]월
            String[] tmp2=tmp1[1].split("일");//tmp2.[0]일
            String[] tmp3=p.getTime().split("시 ");//tmp3.[0]시
            String[] tmp4=tmp3[1].split("분");//tmp4.[0]분
            String cmp=tmp1[0]+tmp2[0]+tmp3[0]+tmp4[0];
            System.out.println(cmp);
            SimpleDateFormat format1 = new SimpleDateFormat ( "MMddHHmm");
            Date cur=new Date();
            String curtime=format1.format(cur);
            Long a1=Long.parseLong(cmp);
            Long a2=Long.parseLong(curtime);
            if((partyJoin!=null)&&(a1>=a2))
                results.add(dto);

        }
        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    public PartyResponseDto joinedparty(long id) {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        List<PartyResponseResultDto> results=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setAddress(p.getAddress());
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            List<Image> itmp=imageRepository.findAllByPartyid(p.getId());
            String[] ist=new String[itmp.size()];
            for(int i=0;i<itmp.size();i++){
                ist[i]=itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            Subscribe issubpresent=subscribeRepository.findByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            if(issubpresent==null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            if(p.getUserid()==id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            resultss.add(dto);
            PartyJoin partyJoin=partyJoinRepository.findPartyJoinByPartyAndUser(p,userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1=p.getDate().split("월 ");//tmp1.[0]월
            String[] tmp2=tmp1[1].split("일");//tmp2.[0]일
            String[] tmp3=p.getTime().split("시 ");//tmp3.[0]시
            String[] tmp4=tmp3[1].split("분");//tmp4.[0]분
            String cmp=tmp1[0]+tmp2[0]+tmp3[0]+tmp4[0];
            System.out.println(cmp);
            SimpleDateFormat format1 = new SimpleDateFormat ( "MMddHHmm");
            Date cur=new Date();
            String curtime=format1.format(cur);
            Long a1=Long.parseLong(cmp);
            Long a2=Long.parseLong(curtime);
            if((partyJoin!=null)&&(a1<a2))
                results.add(dto);

        }
        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    public ResponseDto modifyparty(Long id, PartyModifyDto dto) throws IOException {
        ResponseDto result=new ResponseDto();
        result.setHttp(200);
        result.setStatus(true);
        result.setMsg("수정 성공!");
        try {
            Party party = partyRepository.findById(id).orElse(null);
            if((dto.getImageIndex()!=null)&&(dto.getImageIndex().length!=0)) {
                Integer[] imageIndex = dto.getImageIndex();
                System.out.println(imageIndex.length);
                for (int i = 0; i < imageIndex.length; i++) {
                    Image tmp = imageRepository.findImageByImgIndexAndPartyid(imageIndex[i], id).orElse(null);
                    tmp.setImageSrc(s3Uploader.upload(dto.getImage()[i]));
                    imageRepository.save(tmp);
                }
            }
            party.setTitle(dto.getTitle());
            party.setAddress(dto.getAddress());
            party.setDescription(dto.getDesc());
            party.setTime(dto.getTime());
            party.setMeeting(dto.getMeeting());
            party.setStore(dto.getStore());
            party.setDate(dto.getDate());
            party.setCapacity(dto.getCapacity());
            partyRepository.save(party);
        }
        catch(Exception e){
            result.setMsg("수정 실패...");
            result.setStatus(false);
        }
        return result;
    }

    public PartyResponseDto rawpartyview() {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setAddress(p.getAddress());
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            List<Image> itmp=imageRepository.findAllByPartyid(p.getId());
            String[] ist=new String[itmp.size()];
            for(int i=0;i<itmp.size();i++){
                ist[i]=itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            resultss.add(dto);


        }
        partyResponseDto.setResults(resultss);
        return partyResponseDto;
    }
}
