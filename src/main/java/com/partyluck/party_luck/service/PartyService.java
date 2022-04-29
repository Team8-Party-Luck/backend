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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartyService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PartyRepository partyRepository;
    private final S3Uploader s3Uploader;
    private final PartyJoinRepository partyJoinRepository;
    private final SubscribeRepository subscribeRepository;

    @Autowired
    public PartyService(UserRepository userRepository, ImageRepository imageRepository, PartyRepository partyRepository, S3Uploader s3Uploader, PartyJoinRepository partyJoinRepository, SubscribeRepository subscribeRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.partyRepository = partyRepository;
        this.s3Uploader = s3Uploader;
        this.partyJoinRepository = partyJoinRepository;
        this.subscribeRepository = subscribeRepository;
    }

    public ResponseDto registerparty(MultipartFile[] multipartFile, PartyRequestDto dto, long id) throws IOException {
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
            party.setLocataion(dto.getLocation());
            party.setStore(dto.getStore());
            party.setHost(userRepository.findById(id).orElse(null).getNickname());
            long partyid = partyRepository.save(party).getId();
            int leng = multipartFile.length;
            for (int i = 0; i < leng; i++) {
                Image image = new Image();
                image.setImageSrc(s3Uploader.upload(multipartFile[i]));
                image.setPartyid(partyid);
                imageRepository.save(image);
            }
        }
        catch(Exception e){
            result.setHttp(200);
            result.setMsg("등록 실패...");
            result.setStatus(false);
        }
        return result;


    }

    public PartyResponseDto partyview() {
        PartyResponseDto partyResponseDto=new PartyResponseDto();
        List<Party> parties=partyRepository.findAll();
        List<PartyResponseResultDto> resultss=new ArrayList<>();
        for(Party p:parties){
            PartyResponseResultDto dto=new PartyResponseResultDto();
            dto.setPartyId(p.getId());
            dto.setDate(p.getDate());
            dto.setCapacity(p.getCapacity());
            dto.setLocation(p.getLocataion());
            dto.setHost(p.getHost());
            dto.setTitle(p.getTitle());
            int sizemem=partyJoinRepository.findAllByParty(p).size();
            dto.setMemberCur(sizemem);
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

    public ResponseDto deleteparty(Long id) {
        ResponseDto result=new ResponseDto();
        try{
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
        try {
            PartyJoin partyJoin = new PartyJoin();
            partyJoin.setParty(partyRepository.findById(id).orElse(null));
            partyJoin.setUser(userRepository.findById(id1).orElse(null));
            partyJoinRepository.save(partyJoin);
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
        result.setLocation(party.getLocataion());
        result.setHostid(userRepository.findByNickname(party.getHost()).orElse(null).getId());
        result.setHost(party.getHost());
        result.setStore(party.getStore());
        result.setTitle(party.getTitle());
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
}
