package com.partyluck.party_luck.service.party;

import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.domain.*;
import com.partyluck.party_luck.dto.*;
import com.partyluck.party_luck.dto.party.request.PartyRequestDto;
import com.partyluck.party_luck.dto.party.response.PartyDetailsResponseDto;
import com.partyluck.party_luck.dto.party.response.PartyResponseDto;
import com.partyluck.party_luck.dto.party.response.PartyResponseResultDto;
import com.partyluck.party_luck.dto.party.response.UserlistResponseDto;
import com.partyluck.party_luck.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PartyService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PartyRepository partyRepository;
    private final S3Uploader s3Uploader;
    private final PartyJoinRepository partyJoinRepository;
    private final SubscribeRepository subscribeRepository;
    private final InitialInfoRepository initialInfoRepository;


//파티 등록
    public ResponseDto registerParty(PartyRequestDto dto, long id) throws IOException {
        ResponseDto result = new ResponseDto(true,200,"등록 성공!");
        try {
            Party party = new Party(dto,id);
            long partyid = partyRepository.save(party).getId();
            if ((dto.getImage()) != null && (!dto.getImage()[0].isEmpty())) {
                int leng = dto.getImage().length;
                for (int i = 0; i < leng; i++) {
                    Image image = new Image(s3Uploader,dto,i,partyid);
                    imageRepository.save(image);
                }
            }
            PartyJoin partyJoin = new PartyJoin(userRepository.findById(id).orElse(null),partyRepository.findById(partyid).orElse(null));
            partyJoinRepository.save(partyJoin);
        } catch (Exception e) {
            return new ResponseDto(false,400,"등록 실패...");
        }
        return result;


    }
//파티 지역 조회
    public PartyResponseDto LocalPartyView(long id) {
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            PartyResponseResultDto dto;
            if (p.getUserid() == id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,true,false);
            else if(p.getUserid()!=id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,false,false);
            else if (p.getUserid()==id&&issubpresent!=null)
                dto=new PartyResponseResultDto(p,ist,true,true);
            else
                dto=new PartyResponseResultDto(p,ist,false,true);
            String city1 = initialInfoRepository.findByUserId(id).orElse(null).getCity();
            String region1 = initialInfoRepository.findByUserId(id).orElse(null).getRegion();
            String[] cmpaddresses = p.getAddress().split(" ");
            String cmpaddress = cmpaddresses[0] + " " + cmpaddresses[1];
            if ((city1 + " " + region1).equals(cmpaddress))
                results.add(dto);
        }
        return new PartyResponseDto(results);
    }
    //파티 삭제
    @Transactional
    public ResponseDto deleteParty(Long id, long userDetailsId) {
        if(partyRepository.findById(id).orElse(null).getUserid()==userDetailsId) {
            try {
                if (partyRepository.findById(id).orElse(null) != null) {
                    imageRepository.deleteAllByPartyid(id);
                    partyJoinRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
                    subscribeRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
                    partyRepository.deleteById(id);
                } else
                    return new ResponseDto(false, 500, "존재하지 않는 파티입니다.");
            } catch (Exception e) {
                return new ResponseDto(false,500,"삭제 실패...");
            }
        }
        else
            return new ResponseDto(false,400,"본인 파티만 삭제할 수 있습니다.");
        return new ResponseDto(true,200,"삭제 성공!");
    }
//파티 참가
    public PartyDetailsResponseDto PartyJoin(Long id, long id1) {
        PartyJoin tmp = partyJoinRepository.findPartyJoinByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null);
        PartyDetailsResponseDto result;
        try {
            if (tmp == null) {
                InitialInfo initialInfo = initialInfoRepository.findByUserId(id1).orElse(null);
                Party party = partyRepository.findById(id).orElse(null);
                if (
                        ((party.getAge().equals("전체")) && (party.getGender().equals("모두"))) ||
                                ((party.getAge().equals("전체")) && (initialInfo.getGender().equals(party.getGender()))) ||
                                ((initialInfo.getAge().equals(party.getAge())) && (party.getGender().equals("모두"))) ||
                                ((initialInfo.getAge().equals(party.getAge())) && (initialInfo.getGender().equals(party.getGender())))
                ) {
                    if (partyJoinRepository.findAllByParty(partyRepository.findById(id).orElse(null)).size() < partyRepository.findById(id).orElse(null).getCapacity()) {
                        PartyJoin partyJoin = new PartyJoin(userRepository.findById(id1).orElse(null),partyRepository.findById(id).orElse(null));
                        partyJoinRepository.save(partyJoin);

                        List<Image> itmp = imageRepository.findAllByPartyid(id);
                        String[] ist = new String[itmp.size()];
                        for (int i = 0; i < itmp.size(); i++)
                            ist[i] = itmp.get(i).getImageSrc();
                        List<PartyJoin> partyJoins = partyJoinRepository.findAllByParty(party);
                        String[] urls = new String[partyJoins.size()];
                        for (int i = 0; i < partyJoins.size(); i++)
                            urls[i] = Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
                        Subscribe subscribe1 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                        if (subscribe1 == null&&partyJoin==null)
                            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,false);
                        else if (subscribe1!=null&&partyJoin==null)
                            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,false);
                        else if (subscribe1==null&&partyJoin != null)
                            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,true);
                        else
                            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,true);

                    } else
                        return null;
                } else
                    return null;
            } else
                return null;
        } catch (Exception e) {
            return null;
        }
        return result;
    }
    //파티 참가 취소
    @Transactional
    public PartyDetailsResponseDto PartyOut(Long id, long id1) {
        PartyDetailsResponseDto result=new PartyDetailsResponseDto();
        try {
            if(partyJoinRepository.findPartyJoinByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null)!=null)
            {
                partyJoinRepository.deleteByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null));

                Party party = partyRepository.findById(id).orElse(null);
                List<Image> itmp = imageRepository.findAllByPartyid(id);
                String[] ist = new String[itmp.size()];
                for (int i = 0; i < itmp.size(); i++)
                    ist[i] = itmp.get(i).getImageSrc();
                List<PartyJoin> partyJoins = partyJoinRepository.findAllByParty(party);
                String[] urls = new String[partyJoins.size()];
                for (int i = 0; i < partyJoins.size(); i++)
                    urls[i] = Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
                Subscribe subscribe1 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                if (subscribe1 == null&&partyJoin==null)
                    result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,false);
                else if (subscribe1!=null&&partyJoin==null)
                    result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,false);
                else if (subscribe1==null&&partyJoin!=null)
                    result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,true);
                else
                    result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,true);
            }
            else
                return null;
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    //좋아요
    @Transactional
    public PartyDetailsResponseDto likeParty(Long id, long id1) {

        Subscribe subscribe = subscribeRepository.findByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null);
        if (subscribe == null) {
            Subscribe tmp = new Subscribe(userRepository.findById(id1).orElse(null),partyRepository.findById(id).orElse(null));
            subscribeRepository.save(tmp);

            PartyDetailsResponseDto result;
            Party party = partyRepository.findById(id).orElse(null);
            List<Image> itmp = imageRepository.findAllByPartyid(id);
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++)
                ist[i] = itmp.get(i).getImageSrc();
            List<PartyJoin> partyJoins=partyJoinRepository.findAllByParty(party);
            String[] urls=new String[partyJoins.size()];
            for(int i=0;i<partyJoins.size();i++)
                urls[i]= Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
            Subscribe subscribe1 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (subscribe1 == null&&partyJoin==null)
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,false);
            else if (subscribe1!=null&&partyJoin==null)
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,false);
            else if (subscribe1==null&&partyJoin != null)
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,true);
            else
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,true);
            return result;
        } else {
            subscribeRepository.deleteSubscribeByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null));
            PartyDetailsResponseDto result;
            Party party = partyRepository.findById(id).orElse(null);

            List<Image> itmp = imageRepository.findAllByPartyid(id);
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++)
                ist[i] = itmp.get(i).getImageSrc();
            List<PartyJoin> partyJoins=partyJoinRepository.findAllByParty(party);
            String[] urls=new String[partyJoins.size()];
            for(int i=0;i<partyJoins.size();i++)
                urls[i]= Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
            Subscribe subscribe2 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (subscribe2 == null&&partyJoin==null)
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,false);
            else if (subscribe2!=null&&partyJoin==null)
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,false);
            else if (subscribe2==null&&partyJoin != null)
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,true);
            else
                result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,true);
            return result;
        }
    }
    //상세 페이지 조회
    public PartyDetailsResponseDto PartyDetail(Long id, long id1) {
        PartyDetailsResponseDto result;
        Party party = partyRepository.findById(id).orElse(null);

        List<Image> itmp = imageRepository.findAllByPartyid(id);
        String[] ist = new String[itmp.size()];
        for (int i = 0; i < itmp.size(); i++)
            ist[i] = itmp.get(i).getImageSrc();
        List<PartyJoin> partyJoins=partyJoinRepository.findAllByParty(party);
        String[] urls=new String[partyJoins.size()];
        for(int i=0;i<partyJoins.size();i++)
            urls[i]= Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
        Subscribe subscribe = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
        PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
        if (subscribe == null&&partyJoin==null)
            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,false);
        else if (subscribe!=null&&partyJoin==null)
            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,false);
        else if (subscribe==null&&partyJoin != null)
            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,false,true);
        else
            result = new PartyDetailsResponseDto(party,id,userRepository,partyJoinRepository,ist,urls,true,true);
        return result;
    }

    //좋아요한 파티 조회
    public PartyResponseDto mySubParty(long id) {
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            PartyResponseResultDto dto;
            if (p.getUserid() == id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,true,false);
            else if(p.getUserid()!=id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,false,false);
            else if (p.getUserid()==id&&issubpresent!=null)
                dto=new PartyResponseResultDto(p,ist,true,true);
            else
                dto=new PartyResponseResultDto(p,ist,false,true);
            Subscribe subscribe = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (subscribe!=null) {
                results.add(dto);
            }

        }
        return new PartyResponseDto(results);
    }

    //내가 주최한 파티
    public PartyResponseDto myHostParty(long id) {
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            PartyResponseResultDto dto;
            if (p.getUserid() == id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,true,false);
            else if(p.getUserid()!=id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,false,false);
            else if (p.getUserid()==id&&issubpresent!=null)
                dto=new PartyResponseResultDto(p,ist,true,true);
            else
                dto=new PartyResponseResultDto(p,ist,false,true);
            if (p.getUserid() == id) {
                results.add(dto);
            }
        }
        return new PartyResponseDto(results);
    }

    //참가할 파티
    public PartyResponseDto willjoinParty(long id) {
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            PartyResponseResultDto dto;
            if (p.getUserid() == id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,true,false);
            else if(p.getUserid()!=id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,false,false);
            else if (p.getUserid()==id&&issubpresent!=null)
                dto=new PartyResponseResultDto(p,ist,true,true);
            else
                dto=new PartyResponseResultDto(p,ist,false,true);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
            System.out.println(cmp);
            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);
            Long a1 = Long.parseLong(cmp);
            Long a2 = Long.parseLong(curtime);
            if ((partyJoin != null) && (a1 >= a2))
                results.add(dto);
        }
        return new PartyResponseDto(results);
    }

    //참가한 파티
    public PartyResponseDto joinedParty(long id) {
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            PartyResponseResultDto dto;
            if (p.getUserid() == id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,true,false);
            else if(p.getUserid()!=id&&issubpresent==null)
                dto=new PartyResponseResultDto(p,ist,false,false);
            else if (p.getUserid()==id&&issubpresent!=null)
                dto=new PartyResponseResultDto(p,ist,true,true);
            else
                dto=new PartyResponseResultDto(p,ist,false,true);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
            System.out.println(cmp);
            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);
            Long a1 = Long.parseLong(cmp);
            Long a2 = Long.parseLong(curtime);
            if ((partyJoin != null) && (a1 < a2))
                results.add(dto);

        }
        return new PartyResponseDto(results);
    }
    //파티 수정
    public ResponseDto modifyParty(Long id, PartyRequestDto dto) throws IOException {
        ResponseDto result = new ResponseDto(true,200,"수정 성공!");
        try {
            Party party = partyRepository.findById(id).orElse(null);
            party.setTitle(dto.getTitle());
            party.setAddress(dto.getAddress());
            party.setDescription(dto.getDesc());
            party.setTime(dto.getTime());
            party.setMeeting(dto.getMeeting());
            party.setStore(dto.getStore());
            party.setDate(dto.getDate());
            party.setCapacity(dto.getCapacity());
            party.setAge(dto.getAge());
            party.setGender(dto.getGender());
            party.setXy(dto.getXy());
            party.setPlace_url(dto.getPlace_url());
            partyRepository.save(party);
        } catch (Exception e) {
            return new ResponseDto(false,500,"수정 실패...");
        }
        return result;
    }
    //파티 일반 보기(비로그인)
    public PartyResponseDto RawPartyView(int page) {
        Pageable pageable= PageRequest.of(page,10, Sort.by((Sort.Direction.DESC),"id"));
        Page<Party> parties = partyRepository.findAll(pageable);
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            PartyResponseResultDto dto = new PartyResponseResultDto(p,ist);
            resultss.add(dto);
        }
        return new PartyResponseDto(resultss);
    }

    //등록할 때 유효성 검사
    public boolean checkRegister(PartyRequestDto dto) {
        boolean check=true;
        if(
        dto.getAddress()==null|| dto.getAddress().equals("") ||
        dto.getAge()==null|| dto.getAge().equals("") ||
        dto.getDate()==null|| dto.getDate().equals("") ||
        dto.getTime()==null|| dto.getTime().equals("") ||
        dto.getTitle()==null|| dto.getTitle().equals("")||
        !dto.getDate().contains("-")|| !dto.getTime().contains(":"))
            check= false;
        return check;
    }

    //수정할 때 유효성 검사
    public boolean checkModify(PartyRequestDto dto){
        boolean check=true;
        if(
                dto.getAddress()==null|| dto.getAddress().equals("") ||
                        dto.getAge()==null|| dto.getAge().equals("") ||
                        dto.getDate()==null|| dto.getDate().equals("") ||
                        dto.getTime()==null|| dto.getTime().equals("") ||
                        dto.getTitle()==null|| dto.getTitle().equals("")||
                        !dto.getDate().contains("-")|| !dto.getTime().contains(":"))
            check= false;
        return check;

    }
    //각 파티 참여자 조회
    public List<UserlistResponseDto> Userlist(Long partyid) {
        List<UserlistResponseDto> results= new ArrayList<>();
        List<PartyJoin> tmp=partyJoinRepository.findAllByParty(partyRepository.findById(partyid).orElse(null));
        for(PartyJoin i : tmp){
            String nickname=userRepository.findById(i.getUser().getId()).orElse(null).getNickname();
            String gender=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getGender();
            String age=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getAge();
            String imageUrl=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getProfile_img();
            String city=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getCity();
            String region=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getRegion();
            UserlistResponseDto dto=new UserlistResponseDto(nickname,age,gender,imageUrl,city+" "+region);
            results.add(dto);
        }
        return results;
    }
}
