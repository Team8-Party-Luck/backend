package com.partyluck.party_luck.party;

import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.party.domain.Image;
import com.partyluck.party_luck.party.domain.Party;
import com.partyluck.party_luck.party.domain.PartyJoin;
import com.partyluck.party_luck.party.domain.Subscribe;
import com.partyluck.party_luck.party.repository.PartyJoinRepository;
import com.partyluck.party_luck.party.repository.PartyRepository;
import com.partyluck.party_luck.party.repository.SubscribeRepository;
import com.partyluck.party_luck.party.requestDto.LocalSearchDto;
import com.partyluck.party_luck.party.requestDto.PartyRequestDto;
import com.partyluck.party_luck.party.repository.ImageRepository;
import com.partyluck.party_luck.party.responseDto.*;
import com.partyluck.party_luck.user.domain.InitialInfo;
import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.user.repository.InitialInfoRepository;
import com.partyluck.party_luck.user.repository.UserRepository;
import com.partyluck.party_luck.alarm.Alarm;
import com.partyluck.party_luck.alarm.responseDto.AlarmPageResponseDto;
import com.partyluck.party_luck.alarm.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
    private final AlarmRepository alarmRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;


    //?????? ??????
    public ResponseDto registerParty(PartyRequestDto dto, long id) throws IOException {
        ResponseDto result = new ResponseDto(true, 200, "?????? ??????!");
        try {
            Party party = new Party(dto, id);
            long partyid = partyRepository.save(party).getId();
            if (dto.getDefaultImage() != null && !dto.getDefaultImage().equals(""))
                setDefaultImage(dto.getDefaultImage(), partyid);
            else if ((dto.getImage()) != null && (!dto.getImage()[0].isEmpty())) {
                int leng = dto.getImage().length;
                for (int i = 0; i < leng; i++) {
                    Image image = new Image(s3Uploader, dto, i, partyid);
                    imageRepository.save(image);
                }
            }
            PartyJoin partyJoin = new PartyJoin(userRepository.findById(id).orElse(null), partyRepository.findById(partyid).orElse(null));
            partyJoinRepository.save(partyJoin);
        } catch (Exception e) {
            return new ResponseDto(false, 400, "?????? ??????...");
        }
        return result;


    }

    //?????? ????????? ??????
    private void setDefaultImage(String type, long partyid) {
        if (type.equals("??????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/35864eef-fd80-42db-9e78-3706d98feb32";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("??????/?????????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/dfe05574-3938-463d-a622-f93e8ae04195";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("??????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/0dae4dca-de5d-4651-815a-e730509f764b";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("??????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/3c174935-3ee9-4eb6-a648-a1ffc4c7dba9";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("???????????????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/b682e7d5-7ab7-4a05-a086-825503828236";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("?????????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/aa8e9806-9cd9-484e-9b28-bc3f71ef8316";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("??????/?????????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/a350ea48-cc74-4b89-bdc8-cd060019699d";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        } else if (type.equals("??????")) {
            String src = "https://minibucketjwc.s3.ap-northeast-2.amazonaws.com/static/image/a4596ad8-ff63-4c01-997e-d715f600cb72";
            Image image = new Image(src, 1, partyid);
            imageRepository.save(image);
        }


    }

    //?????? ?????? ??????
    public PartyResponseDto LocalPartyView(long id, LocalSearchDto localSearchDto) {
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
            if (p.getUserid() == id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, true, false,partyJoinRepository);
            else if (p.getUserid() != id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, false, false,partyJoinRepository);
            else if (p.getUserid() == id && issubpresent != null)
                dto = new PartyResponseResultDto(p, ist, true, true,partyJoinRepository);
            else
                dto=new PartyResponseResultDto(p,ist,false,true,partyJoinRepository);
//            String city1 = initialInfoRepository.findByUserId(id).orElse(null).getCity();
//            String region1 = initialInfoRepository.findByUserId(id).orElse(null).getRegion();
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);
            Long a1 = Long.parseLong(cmp);
            Long a2 = Long.parseLong(curtime);
            if(!localSearchDto.getAnswer().contains(" ")){
                String[] cmpaddresses = p.getAddress().split(" ");
                String cmpaddress = cmpaddresses[0];
                if ((localSearchDto.getAnswer()).equals(cmpaddress)&&(a1>=a2))
                    results.add(dto);
            }
            else{
                String[] cmpaddresses = p.getAddress().split(" ");
                String cmpaddress = cmpaddresses[0] + " " + cmpaddresses[1];
                if ((localSearchDto.getAnswer()).equals(cmpaddress)&&(a1>=a2))
                    results.add(dto);

            }
        }
        return new PartyResponseDto(results);
    }

    //?????? ??????
    @Transactional
    public ResponseDto deleteParty(Long id, long userDetailsId) {
        if (partyRepository.findById(id).orElse(null).getUserid() == userDetailsId) {
            try {
                if (partyRepository.findById(id).orElse(null) != null) {
                    imageRepository.deleteAllByPartyid(id);
                    partyJoinRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
                    subscribeRepository.deleteAllByParty(partyRepository.findById(id).orElse(null));
                    partyRepository.deleteById(id);

                    //???????????? ??????
                } else
                    return new ResponseDto(false, 500, "???????????? ?????? ???????????????.");
            } catch (Exception e) {
                return new ResponseDto(false, 500, "?????? ??????...");
            }
        } else
            return new ResponseDto(false, 400, "?????? ????????? ????????? ??? ????????????.");
        return new ResponseDto(true, 200, "?????? ??????!");
    }

    //?????? ??????
    public PartyDetailsResponseDto PartyJoin(Long id, long id1) {
        PartyJoin tmp = partyJoinRepository.findPartyJoinByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null);
        PartyDetailsResponseDto result;
        try {
            if (tmp == null) {
                InitialInfo initialInfo = initialInfoRepository.findByUserId(id1).orElse(null);
                Party party = partyRepository.findById(id).orElse(null);
                if ((checkAge(party, initialInfo) && party.getGender().equals("??????")) ||
                        (checkAge(party, initialInfo) && party.getGender().equals(initialInfo.getGender()))) {
                    if (partyJoinRepository.findAllByParty(partyRepository.findById(id).orElse(null)).size() < partyRepository.findById(id).orElse(null).getCapacity()) {
                        PartyJoin partyJoin = new PartyJoin(userRepository.findById(id1).orElse(null), partyRepository.findById(id).orElse(null));
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
                        if (subscribe1 == null && partyJoin == null)
                            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, false);
                        else if (subscribe1 != null && partyJoin == null)
                            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, false);
                        else if (subscribe1 == null && partyJoin != null)
                            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, true);
                        else
                            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, true);

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

    //?????? ?????????
    private boolean checkAge(Party party, InitialInfo initialInfo) {
        String[] ages = party.getAge().split(" ");
        if (Arrays.asList(ages).contains("??????") || Arrays.asList(ages).contains(initialInfo.getAge()))
            return true;
        return false;
    }

    //?????? ?????? ??????
    @Transactional
    public PartyDetailsResponseDto PartyOut(Long id, long id1) {
        PartyDetailsResponseDto result;
        try {
            if (partyJoinRepository.findPartyJoinByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null) != null) {
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
                if (subscribe1 == null && partyJoin == null)
                    result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, false);
                else if (subscribe1 != null && partyJoin == null)
                    result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, false);
                else if (subscribe1 == null && partyJoin != null)
                    result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, true);
                else
                    result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, true);
            } else
                return null;
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    //?????????
    @Transactional
    public PartyDetailsResponseDto likeParty(Long id, long id1) {

        Subscribe subscribe = subscribeRepository.findByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null)).orElse(null);
        if (subscribe == null) {
            Subscribe tmp = new Subscribe(userRepository.findById(id1).orElse(null), partyRepository.findById(id).orElse(null));
            subscribeRepository.save(tmp);

            PartyDetailsResponseDto result;
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
            if (subscribe1 == null && partyJoin == null)
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, false);
            else if (subscribe1 != null && partyJoin == null)
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, false);
            else if (subscribe1 == null && partyJoin != null)
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, true);
            else
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, true);
            return result;
        } else {
            subscribeRepository.deleteSubscribeByPartyAndUser(partyRepository.findById(id).orElse(null), userRepository.findById(id1).orElse(null));
            PartyDetailsResponseDto result;
            Party party = partyRepository.findById(id).orElse(null);

            List<Image> itmp = imageRepository.findAllByPartyid(id);
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++)
                ist[i] = itmp.get(i).getImageSrc();
            List<PartyJoin> partyJoins = partyJoinRepository.findAllByParty(party);
            String[] urls = new String[partyJoins.size()];
            for (int i = 0; i < partyJoins.size(); i++)
                urls[i] = Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
            Subscribe subscribe2 = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
            if (subscribe2 == null && partyJoin == null)
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, false);
            else if (subscribe2 != null && partyJoin == null)
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, false);
            else if (subscribe2 == null && partyJoin != null)
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, true);
            else
                result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, true);
            return result;
        }
    }

    //?????? ????????? ??????
    public PartyDetailsResponseDto PartyDetail(Long id, long id1) {
        PartyDetailsResponseDto result;
        Party party = partyRepository.findById(id).orElse(null);

        List<Image> itmp = imageRepository.findAllByPartyid(id);
        String[] ist = new String[itmp.size()];
        for (int i = 0; i < itmp.size(); i++)
            ist[i] = itmp.get(i).getImageSrc();
        List<PartyJoin> partyJoins = partyJoinRepository.findAllByParty(party);
        String[] urls = new String[partyJoins.size()];
        for (int i = 0; i < partyJoins.size(); i++)
            urls[i] = Objects.requireNonNull(initialInfoRepository.findByUserId(partyJoins.get(i).getUser().getId()).orElse(null)).getProfile_img();
        Subscribe subscribe = subscribeRepository.findByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
        PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(party, userRepository.findById(id1).orElse(null)).orElse(null);
        if (subscribe == null && partyJoin == null)
            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, false);
        else if (subscribe != null && partyJoin == null)
            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, false);
        else if (subscribe == null && partyJoin != null)
            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, false, true);
        else
            result = new PartyDetailsResponseDto(party, id, userRepository, partyJoinRepository, ist, urls, true, true);
        return result;
    }

    //???????????? ?????? ??????
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
            if (p.getUserid() == id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, true, false,partyJoinRepository);
            else if (p.getUserid() != id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, false, false,partyJoinRepository);
            else if (p.getUserid() == id && issubpresent != null)
                dto = new PartyResponseResultDto(p, ist, true, true,partyJoinRepository);
            else
                dto = new PartyResponseResultDto(p, ist, false, true,partyJoinRepository);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);
            Long a1 = Long.parseLong(cmp);
            Long a2 = Long.parseLong(curtime);
            Subscribe subscribe = subscribeRepository.findByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            if (subscribe != null&&(a1>=a2)) {
                results.add(dto);
            }

        }
        return new PartyResponseDto(results);
    }

    //?????? ????????? ??????
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
            if (p.getUserid() == id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, true, false,partyJoinRepository);
            else if (p.getUserid() != id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, false, false,partyJoinRepository);
            else if (p.getUserid() == id && issubpresent != null)
                dto = new PartyResponseResultDto(p, ist, true, true,partyJoinRepository);
            else
                dto = new PartyResponseResultDto(p, ist, false, true,partyJoinRepository);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);
            Long a1 = Long.parseLong(cmp);
            Long a2 = Long.parseLong(curtime);
            if (p.getUserid() == id&&(a1>=a2)) {
                results.add(dto);
            }
        }
        return new PartyResponseDto(results);
    }

    //????????? ??????
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
            if (p.getUserid() == id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, true, false,partyJoinRepository);
            else if (p.getUserid() != id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, false, false,partyJoinRepository);
            else if (p.getUserid() == id && issubpresent != null)
                dto = new PartyResponseResultDto(p, ist, true, true,partyJoinRepository);
            else
                dto = new PartyResponseResultDto(p, ist, false, true,partyJoinRepository);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
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

    //????????? ??????
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
            if (p.getUserid() == id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, true, false,partyJoinRepository);
            else if (p.getUserid() != id && issubpresent == null)
                dto = new PartyResponseResultDto(p, ist, false, false,partyJoinRepository);
            else if (p.getUserid() == id && issubpresent != null)
                dto = new PartyResponseResultDto(p, ist, true, true,partyJoinRepository);
            else
                dto = new PartyResponseResultDto(p, ist, false, true,partyJoinRepository);
            PartyJoin partyJoin = partyJoinRepository.findPartyJoinByPartyAndUser(p, userRepository.findById(id).orElse(null)).orElse(null);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
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

    //?????? ??????
    public ResponseDto modifyParty(Long id, PartyRequestDto dto) throws IOException {
        ResponseDto result = new ResponseDto(true, 200, "?????? ??????!");
        try {
            Party party = partyRepository.findById(id).orElse(null);

            //???????????? ????????? ????????? ??????
            //?????? ????????? ????????? ???????????? ????????? ??? ????????????
            String alarmMessage = "";
            List<String> alarms=new ArrayList<>();
            if (dto.getCapacity() != party.getCapacity()) {
                alarmMessage = "?????? ?????? ?????????????????????";
                alarms.add(alarmMessage);
            }
            if (!Objects.equals(dto.getAddress(), party.getAddress())) {
                alarmMessage = "????????? ????????? ?????????????????????";
                alarms.add(alarmMessage);
            }
            if (!Objects.equals(dto.getStore(), party.getStore())) {
                alarmMessage = "???????????? ?????????????????????";
                alarms.add(alarmMessage);
            }
            if ((!Objects.equals(dto.getDate(), party.getDate()))
                    || (!Objects.equals(dto.getTime(), party.getTime()))) {
                alarmMessage = "?????? ????????? ?????????????????????";
                alarms.add(alarmMessage);
            }
            if (!Objects.equals(dto.getMeeting(), party.getMeeting())) {
                alarmMessage = "????????? ????????? ?????????????????????";
                alarms.add(alarmMessage);
            }
            if (!Objects.equals(dto.getDesc(), party.getDescription())) {
                alarmMessage = "??????????????? ?????????????????????";
                alarms.add(alarmMessage);
            }
            if (!Objects.equals(dto.getGender(), party.getGender())) {
                alarmMessage = "?????? ????????? ?????????????????????";
                alarms.add(alarmMessage);
            }

            //??????
            party.setTitle(dto.getTitle());
            party.setAddress(dto.getAddress());
            party.setDescription(dto.getDesc());
            party.setTime(dto.getTime());
            party.setMeeting(dto.getMeeting());
            party.setStore(dto.getStore());
            party.setDate(dto.getDate());
            party.setCapacity(dto.getCapacity());
            List<String> ages = dto.getAge();
            Collections.sort(ages);
            String s = "";
            for (int i = 0; i < ages.size(); i++) {
                s += ages.get(i) + " ";
            }
            party.setAge(s.substring(0, s.length() - 1));
            party.setGender(dto.getGender());
            party.setXy(dto.getXy());
            party.setPlace_url(dto.getPlace_url());
            partyRepository.save(party);

            //????????? ????????? ??????
            String image = imageRepository.findImageByImgIndexAndPartyid(1, id).get().getImageSrc();
            String title = partyRepository.findById(id).get().getTitle();
            String store = partyRepository.findById(id).get().getStore();

            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);

            //??????????????? - ????????? ?????? ?????????????????? ??? ????????????
            List<PartyJoin> tmp=partyJoinRepository.findAllByParty(partyRepository.findById(id).orElse(null));
            for(PartyJoin p : tmp){
                User user = p.getUser();
                AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(image, title, store, alarms, curtime,user.getId());
                Alarm alarm = new Alarm(alarmPageResponseDto, id, user, curtime);
                alarmRepository.save(alarm);
                messagingTemplate.convertAndSend("/alarm/"+user.getId().toString(),alarmPageResponseDto); //destination ???????????? ???????????????
                String topic=channelTopic.getTopic();
                redisTemplate.convertAndSend(topic, alarmPageResponseDto);
            }


        } catch (Exception e) {
            return new ResponseDto(false, 500, "?????? ??????...");
        }
        return result;
    }

    //?????? ?????? ??????(????????????)
    public PartyResponseDto RawPartyView(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by((Sort.Direction.DESC), "id"));
        List<Party> parties = partyRepository.findAll();
        List<PartyResponseResultDto> resultss = new ArrayList<>();
        for (Party p : parties) {
            List<Image> itmp = imageRepository.findAllByPartyid(p.getId());
            String[] ist = new String[itmp.size()];
            for (int i = 0; i < itmp.size(); i++) {
                ist[i] = itmp.get(i).getImageSrc();
            }
            PartyResponseResultDto dto = new PartyResponseResultDto(p, ist,partyJoinRepository);
            String[] tmp1 = p.getDate().split("-");
            String[] tmp3 = p.getTime().split(":");
            String cmp = tmp1[0] + tmp1[1] + tmp3[0] + tmp3[1];
            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);
            Long a1 = Long.parseLong(cmp);
            Long a2 = Long.parseLong(curtime);
            if(a1>=a2) {
                resultss.add(dto);
            }
        }
        Collections.reverse(resultss);
        List<PartyResponseResultDto> results = new ArrayList<>();
        for(int i=page*10-10;i<page*10;i++){
            try{
                results.add(resultss.get(i));
            }catch(Exception e){
                return new PartyResponseDto(results);
            }
        }
        return new PartyResponseDto(results);
    }

    //????????? ??? ????????? ??????
    public boolean checkRegister(PartyRequestDto dto) {
        boolean check = true;
        if (
                dto.getAddress() == null || dto.getAddress().equals("") ||
                        dto.getAge() == null || dto.getAge().equals("") ||
                        dto.getDate() == null || dto.getDate().equals("") ||
                        dto.getTime() == null || dto.getTime().equals("") ||
                        dto.getTitle() == null || dto.getTitle().equals("") ||
                        !dto.getDate().contains("-") || !dto.getTime().contains(":"))
            check = false;
        return check;
    }

    //????????? ??? ????????? ??????
    public boolean checkModify(PartyRequestDto dto) {
        boolean check = true;
        if (
                dto.getAddress() == null || dto.getAddress().equals("") ||
                        dto.getAge() == null || dto.getAge().equals("") ||
                        dto.getDate() == null || dto.getDate().equals("") ||
                        dto.getTime() == null || dto.getTime().equals("") ||
                        dto.getTitle() == null || dto.getTitle().equals("") ||
                        !dto.getDate().contains("-") || !dto.getTime().contains(":"))
            check = false;
        return check;

    }

    //??? ?????? ????????? ??????
    public UserlistResultDto Userlist(Long partyid) {
        List<UserlistResponseDto> results= new ArrayList<>();
        List<PartyJoin> tmp=partyJoinRepository.findAllByParty(partyRepository.findById(partyid).orElse(null));
        Long hostId=partyRepository.findById(partyid).orElse(null).getUserid();
        for(PartyJoin i : tmp){
            Long userId=i.getUser().getId();
            String nickname=userRepository.findById(i.getUser().getId()).orElse(null).getNickname();
            String gender=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getGender();
            String age=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getAge();
            String imageUrl=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getProfile_img();
            String city=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getCity();
            String region=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getRegion();
            String sns=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getSns_url();
            String intro=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getIntro();
            String[] foods=initialInfoRepository.findByUserId(i.getUser().getId()).orElse(null).getFood().split(" ");
            UserlistResponseDto dto=new UserlistResponseDto(userId,nickname,age,gender,imageUrl,city+" "+region,sns,intro,foods);

            results.add(dto);
        }
        return new UserlistResultDto(hostId, results);
    }
}