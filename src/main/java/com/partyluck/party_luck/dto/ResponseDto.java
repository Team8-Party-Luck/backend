package com.partyluck.party_luck.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    private boolean status;
    private int http;
    private String msg;

    public ResponseDto(boolean status, int http, String msg){
        this.status=status;
        this.http=http;
        this.msg=msg;
    }

    public ResponseDto() {

    }
}
