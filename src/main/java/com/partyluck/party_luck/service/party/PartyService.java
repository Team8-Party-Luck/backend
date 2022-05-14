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
    public ResponseDto registerparty(PartyRequestDto dto, long id) throws IOException {
        ResponseDto result = new ResponseDto();
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
            party.setAge(dto.getAge());
            party.setGender(dto.getGender());
            party.setXy(dto.getXy());
            party.setPlace_url(dto.getPlace_url());
            party.setUserid(id);
            long partyid = partyRepository.save(party).getId();
            if ((dto.getImage()) != null && (!dto.getImage()[0].isEmpty())) {
                int leng = dto.getImage().length;
                for (int i = 0; i < leng; i++) {
                    Image image = new Image();
                    image.setImageSrc(s3Uploader.upload(dto.getImage()[i]));
                    image.setPartyid(partyid);
                    image.setImgIndex(i + 1);
                    imageRepository.save(image);
                }
            }
            PartyJoin partyJoin = new PartyJoin();
            partyJoin.setParty(partyRepository.findById(partyid).orElse(null));
            partyJoin.setUser(userRepository.findById(id).orElse(null));
            partyJoinRepository.save(partyJoin);
        } catch (Exception e) {
            result.setHttp(200);
            result.setMsg("등록 실패...");
            result.setStatus(false);
        }
        return result;


    }
//파티 지역 조회
    public PartyResponseDto partyview(long id, Integer pageid) {
        PartyResponseDto partyResponseDto = new PartyResponseDto();
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            PartyResponseResultDto dto = new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            String[] tmp=p.getAddress().split(" ");
            String addr=tmp[0]+" "+tmp[1];
            dto.setAddress(addr);
            dto.setTitle(p.getTitle());
            dto.setStore(p.getStore());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setDesc(p.getDescription());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            if (p.getUserid() == id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (issubpresent == null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            resultss.add(dto);
            String city1 = initialInfoRepository.findByUserId(id).orElse(null).getCity();
            String region1 = initialInfoRepository.findByUserId(id).orElse(null).getRegion();
            String[] cmpaddresses = p.getAddress().split(" ");
            String cmpaddress = cmpaddresses[0] + " " + cmpaddresses[1];
            if ((city1 + " " + region1).equals(cmpaddress))
                results.add(dto);

        }

        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    @Transactional
    public ResponseDto deleteparty(Long id, long userDetailsId) {
        ResponseDto result = new ResponseDto();
        if(partyRepository.findById(id).orElse(null).getUserid()==userDetailsId) {
            try {
                if (partyRepository.findById(id).orElse(null) != null) {
                    imageRepository.deleteAllByPartyid(id);
                    partyJoinRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
                    subscribeRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
                    partyRepository.deleteById(id);
                } else
                    return new ResponseDto(false, 200, "존재하지 않는 파티입니다.");
            } catch (Exception e) {
                result.setHttp(200);
                result.setStatus(false);
                result.setMsg("삭제 실패...");
                return result;
            }
        }
        else
            result=new ResponseDto(false,200,"본인 파티만 삭제할 수 있습니다.");
        result.setHttp(200);
        result.setStatus(true);
        result.setMsg("삭제 성공!");
        return result;
    }
//파티 참가
    public PartyDetailsResponseDto partyjoin(Long id, long id1) {
//        String result = "";
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
                        PartyJoin partyJoin = new PartyJoin();
                        partyJoin.setParty(partyRepository.findById(id).orElse(null));
                        partyJoin.setUser(userRepository.findById(id1).orElse(null));
                        partyJoinRepository.save(partyJoin);

                        result = new PartyDetailsResponseDto();
                        result.setCapacity(party.getCapacity());
                        result.setDate(party.getDate());
                        result.setDesc(party.getDescription());
                        result.setPartyid(id);
                        result.setStore(party.getStore());
                        String[] addtmp = party.getAddress().split(" ");
                        result.setAddress(addtmp[0] + " " + addtmp[1]);
                        result.setHostid(userRepository.findById(party.getUserid()).orElse(null).getId());
                        result.setHost(userRepository.findById(party.getUserid()).orElse(null).getNickname());
                        result.setTitle(party.getTitle());
                        result.setMeeting(party.getMeeting());
                        result.setTime(party.getTime());
                        result.setAge(party.getAge());
                        result.setGender(party.getGender());
                        result.setXy(party.getXy());
                        result.setPlace_url(party.getPlace_url());
                        result.setMemberCnt(partyJoinRepository.findAllByParty(party).size());
                        List<Image> itmp = imageRepository.findAllByPartyid(id);
                        String[] ist = new String[itmp.size()];
                        for (int i = 0; i < itmp.size(); i++)
                            ist[i] = itmp.get(i).getImageSrc();
                        result.setImage(ist);
                        List<PartyJoin> partyJoins = partyJoinRepository.findAllByParty(party);
                        String[] urls = new String[partyJoins.size()];
                        for (int i = 0; i < partyJoins.size(); i++)
                            urls[i] = Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
                        result.setUserimageurls(urls);
                        Subscribe subscribe1 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                        if (subscribe1 == null)
                            result.setSub(false);
                        else
                            result.setSub(true);
//                        PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                        if (partyJoin == null)
                            result.setJoin(false);
                        else
                            result.setJoin(true);

                    } else
                        return null;
                } else
                    return null;
            } else
                return null;
        } catch (Exception e) {
//            result = "참가 실패...";
            return null;
        }
//        result = "참가가 완료되었습니다.";
        return result;
    }

    @Transactional
    public PartyDetailsResponseDto partyout(Long id, long id1) {
        PartyDetailsResponseDto result=new PartyDetailsResponseDto();
        try {
            if(partyJoinRepository.findPartyJoinByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null)!=null)
            {
                partyJoinRepository.deleteByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null));

                Party party = partyRepository.findById(id).orElse(null);
                result.setCapacity(party.getCapacity());
                result.setDate(party.getDate());
                result.setDesc(party.getDescription());
                result.setPartyid(id);
                result.setStore(party.getStore());
                String[] addtmp = party.getAddress().split(" ");
                result.setAddress(addtmp[0] + " " + addtmp[1]);
                result.setHostid(userRepository.findById(party.getUserid()).orElse(null).getId());
                result.setHost(userRepository.findById(party.getUserid()).orElse(null).getNickname());
                result.setTitle(party.getTitle());
                result.setMeeting(party.getMeeting());
                result.setTime(party.getTime());
                result.setAge(party.getAge());
                result.setGender(party.getGender());
                result.setXy(party.getXy());
                result.setPlace_url(party.getPlace_url());
                result.setMemberCnt(partyJoinRepository.findAllByParty(party).size());
                List<Image> itmp = imageRepository.findAllByPartyid(id);
                String[] ist = new String[itmp.size()];
                for (int i = 0; i < itmp.size(); i++)
                    ist[i] = itmp.get(i).getImageSrc();
                result.setImage(ist);
                List<PartyJoin> partyJoins = partyJoinRepository.findAllByParty(party);
                String[] urls = new String[partyJoins.size()];
                for (int i = 0; i < partyJoins.size(); i++)
                    urls[i] = Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
                result.setUserimageurls(urls);
                Subscribe subscribe1 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                if (subscribe1 == null)
                    result.setSub(false);
                else
                    result.setSub(true);
                PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
                if (partyJoin == null)
                    result.setJoin(false);
                else
                    result.setJoin(true);
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
    public PartyDetailsResponseDto likeparty(Long id, long id1) {

        Subscribe subscribe = subscribeRepository.findByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null);
        if (subscribe == null) {
            Subscribe tmp = new Subscribe();
            tmp.setParty(partyRepository.findById(id).orElse(null));
            tmp.setUser(userRepository.findById(id1).orElse(null));
            subscribeRepository.save(tmp);
            PartyDetailsResponseDto result = new PartyDetailsResponseDto();
            Party party = partyRepository.findById(id).orElse(null);
            result.setCapacity(party.getCapacity());
            result.setDate(party.getDate());
            result.setDesc(party.getDescription());
            result.setPartyid(id);
            result.setStore(party.getStore());
            String[] addtmp=party.getAddress().split(" ");
            result.setAddress(addtmp[0]+" "+addtmp[1]);
            result.setHostid(userRepository.findById(party.getUserid()).orElse(null).getId());
            result.setHost(userRepository.findById(party.getUserid()).orElse(null).getNickname());
            result.setTitle(party.getTitle());
            result.setMeeting(party.getMeeting());
            result.setTime(party.getTime());
            result.setAge(party.getAge());
            result.setGender(party.getGender());
            result.setXy(party.getXy());
            result.setPlace_url(party.getPlace_url());
            result.setMemberCnt(partyJoinRepository.findAllByParty(party).size());
            List<Image> itmp = imageRepository.findAllByPartyid(id);
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++)
                ist[i] = itmp.get(i).getImageSrc();
            result.setImage(ist);
            List<PartyJoin> partyJoins=partyJoinRepository.findAllByParty(party);
            String[] urls=new String[partyJoins.size()];
            for(int i=0;i<partyJoins.size();i++)
                urls[i]= Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
            result.setUserimageurls(urls);
            Subscribe subscribe1 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (subscribe1 == null)
                result.setSub(false);
            else
                result.setSub(true);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (partyJoin == null)
                result.setJoin(false);
            else
                result.setJoin(true);
            return result;
        } else {
            subscribeRepository.deleteSubscribeByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null));
            PartyDetailsResponseDto result = new PartyDetailsResponseDto();
            Party party = partyRepository.findById(id).orElse(null);
            result.setCapacity(party.getCapacity());
            result.setDate(party.getDate());
            result.setDesc(party.getDescription());
            result.setPartyid(id);
            result.setStore(party.getStore());
            String[] addtmp=party.getAddress().split(" ");
            result.setAddress(addtmp[0]+" "+addtmp[1]);
            result.setHostid(userRepository.findById(party.getUserid()).orElse(null).getId());
            result.setHost(userRepository.findById(party.getUserid()).orElse(null).getNickname());
            result.setTitle(party.getTitle());
            result.setMeeting(party.getMeeting());
            result.setTime(party.getTime());
            result.setAge(party.getAge());
            result.setGender(party.getGender());
            result.setXy(party.getXy());
            result.setPlace_url(party.getPlace_url());
            result.setMemberCnt(partyJoinRepository.findAllByParty(party).size());
            List<Image> itmp = imageRepository.findAllByPartyid(id);
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++)
                ist[i] = itmp.get(i).getImageSrc();
            result.setImage(ist);
            List<PartyJoin> partyJoins=partyJoinRepository.findAllByParty(party);
            String[] urls=new String[partyJoins.size()];
            for(int i=0;i<partyJoins.size();i++)
                urls[i]= Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
            result.setUserimageurls(urls);
            Subscribe subscribe2 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (subscribe2 == null)
                result.setSub(false);
            else
                result.setSub(true);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (partyJoin == null)
                result.setJoin(false);
            else
                result.setJoin(true);
            return result;

        }

    }

    //상세 페이지 조회
    public PartyDetailsResponseDto partydetail(Long id, long id1) {
        PartyDetailsResponseDto result = new PartyDetailsResponseDto();
        Party party = partyRepository.findById(id).orElse(null);
        result.setCapacity(party.getCapacity());
        result.setDate(party.getDate());
        result.setDesc(party.getDescription());
        result.setPartyid(id);
        result.setStore(party.getStore());
        String[] addtmp=party.getAddress().split(" ");
        result.setAddress(addtmp[0]+" "+addtmp[1]);
        result.setHostid(userRepository.findById(party.getUserid()).orElse(null).getId());
        result.setHost(userRepository.findById(party.getUserid()).orElse(null).getNickname());
        result.setTitle(party.getTitle());
        result.setMeeting(party.getMeeting());
        result.setTime(party.getTime());
        result.setAge(party.getAge());
        result.setGender(party.getGender());
        result.setPlace_url(party.getPlace_url());
        result.setXy(party.getXy());
        result.setMemberCnt(partyJoinRepository.findAllByParty(party).size());
        List<Image> itmp = imageRepository.findAllByPartyid(id);
        String[] ist = new String[itmp.size()];
        for (int i = 0; i < itmp.size(); i++)
            ist[i] = itmp.get(i).getImageSrc();
        result.setImage(ist);
        List<PartyJoin> partyJoins=partyJoinRepository.findAllByParty(party);
        String[] urls=new String[partyJoins.size()];
        for(int i=0;i<partyJoins.size();i++)
            urls[i]= Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
        result.setUserimageurls(urls);
        Subscribe subscribe = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
        if (subscribe == null)
            result.setSub(false);
        else
            result.setSub(true);
        PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
        if (partyJoin == null)
            result.setJoin(false);
        else
            result.setJoin(true);
        return result;


    }

    //좋아요한 파티 조회
    public PartyResponseDto mysubparty(long id, Integer pageid) {
        PartyResponseDto partyResponseDto = new PartyResponseDto();
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            PartyResponseResultDto dto = new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            String[] tmp=p.getAddress().split(" ");
            String addr=tmp[0]+" "+tmp[1];
            dto.setAddress(addr);
            dto.setDesc(p.getDescription());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            if (p.getUserid() == id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            dto.setIssub(false);
            resultss.add(dto);
            Subscribe subscribe = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (subscribe!=null) {
                dto.setIssub(true);
                results.add(dto);
            }

        }

        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    //내가 주최한 파티
    public PartyResponseDto myhostparty(long id, Integer pageid) {
        PartyResponseDto partyResponseDto = new PartyResponseDto();
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            PartyResponseResultDto dto = new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            String[] tmp=p.getAddress().split(" ");
            String addr=tmp[0]+" "+tmp[1];
            dto.setAddress(addr);
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            dto.setIshost(false);
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (issubpresent == null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            resultss.add(dto);
            if (p.getUserid() == id) {
                dto.setIshost(true);
                results.add(dto);
            }
        }

        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    //참가할 파티
    public PartyResponseDto willjoinparty(long id, Integer pageid) {
        PartyResponseDto partyResponseDto = new PartyResponseDto();
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            PartyResponseResultDto dto = new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            String[] tmp=p.getAddress().split(" ");
            String addr=tmp[0]+" "+tmp[1];
            dto.setAddress(addr);
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (issubpresent == null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            if (p.getUserid() == id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            resultss.add(dto);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1 = p.getDate().split("-");//tmp1.[0]월
//            String[] tmp2 = tmp1[1].split("일");//tmp2.[0]일
            String[] tmp3 = p.getTime().split(":");//tmp3.[0]시
//            String[] tmp4 = tmp3[1].split("분");//tmp4.[0]분
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
        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    //참가한 파티
    public PartyResponseDto joinedparty(long id, Integer pageid) {
        PartyResponseDto partyResponseDto = new PartyResponseDto();
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        List<PartyResponseResultDto> results = new ArrayList<>();
        for (Party p : parties) {
            PartyResponseResultDto dto = new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            String[] tmp=p.getAddress().split(" ");
            String addr=tmp[0]+" "+tmp[1];
            dto.setAddress(addr);
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            Subscribe issubpresent = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (issubpresent == null)
                dto.setIssub(false);
            else
                dto.setIssub(true);
            if (p.getUserid() == id)
                dto.setIshost(true);
            else
                dto.setIshost(false);
            resultss.add(dto);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1 = p.getDate().split("-");//tmp1.[0]월
//            String[] tmp2 = tmp1[1].split("일");//tmp2.[0]일
            String[] tmp3 = p.getTime().split(":");//tmp3.[0]시
//            String[] tmp4 = tmp3[1].split("분");//tmp4.[0]분
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

        partyResponseDto.setResults(results);
        return partyResponseDto;
    }

    public ResponseDto modifyparty(Long id, PartyRequestDto dto) throws IOException {
        ResponseDto result = new ResponseDto();
        result.setHttp(200);
        result.setStatus(true);
        result.setMsg("수정 성공!");
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
            result.setMsg("수정 실패...");
            result.setStatus(false);
        }
        return result;
    }

    public PartyResponseDto rawpartyview(int page) {
        PartyResponseDto partyResponseDto = new PartyResponseDto();
        Pageable pageable= PageRequest.of(page,10, Sort.by((Sort.Direction.DESC),"id"));
        Page<Party> parties = partyRepository.findAll(pageable);
//        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        for (Party p : parties) {
            PartyResponseResultDto dto = new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            String[] tmp=p.getAddress().split(" ");
            String addr=tmp[0]+" "+tmp[1];
            dto.setAddress(addr);
            dto.setTitle(p.getTitle());
            dto.setMeeting(p.getMeeting());
            dto.setTime(p.getTime());
            dto.setStore(p.getStore());
            dto.setDesc(p.getDescription());
            dto.setAge(p.getAge());
            dto.setGender(p.getGender());
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            dto.setImage(ist);
            resultss.add(dto);


        }
        partyResponseDto.setResults(resultss);
        return partyResponseDto;
    }


    //등록할 때 유효성 검사
    public boolean checkregister(PartyRequestDto dto) {
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
    public boolean checkmodify(PartyRequestDto dto){
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
    public List<UserlistResponseDto> userlist(Long partyid) {
        List<UserlistResponseDto> results= new ArrayList<>();
        List<PartyJoin> tmp=partyJoinRepository.findAllByParty(partyRepository.findById(partyid).orElse(null));
        for(PartyJoin i : tmp){
            UserlistResponseDto dto=new UserlistResponseDto();
            dto.setNickname(userRepository.findById(i.getUser().getId()).orElse(null).getNickname());
            dto.setGender(initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getGender());
            dto.setAge(initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getAge());
            dto.setImageUrl(initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getProfile_img());
            String city=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getCity();
            String region=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getRegion();
            dto.setLocation(city+" "+region);
            results.add(dto);
        }

        return results;
    }
}
