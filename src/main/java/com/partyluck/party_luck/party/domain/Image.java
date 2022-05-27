package com.partyluck.party_luck.user.controller.party.domain;

import com.partyluck.party_luck.config.S3Uploader;
import com.partyluck.party_luck.user.controller.party.requestDto.PartyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.IOException;

@Getter
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageSrc;

    private Integer imgIndex;

    private Long partyid;

    public Image(S3Uploader s3Uploader, PartyRequestDto dto, int i, long partyid) throws IOException {
        this.imageSrc=s3Uploader.upload(dto.getImage()[i]);
        this.imgIndex=i+1;
        this.partyid=partyid;
    }
    public Image(String imageSrc, int imgIndex, long partyid){
        this.imageSrc=imageSrc;
        this.imgIndex=imgIndex;
        this.partyid=partyid;
    }
}
