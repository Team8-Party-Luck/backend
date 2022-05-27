package com.partyluck.party_luck.user.controller.party.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseDto {
    private boolean status;
    private int http;
    private String msg;

    public ResponseDto(boolean status, int http, String msg){
        this.status=status;
        this.http=http;
        this.msg=msg;
    }
}
