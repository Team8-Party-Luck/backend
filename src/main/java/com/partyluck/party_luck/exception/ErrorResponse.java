package com.partyluck.party_luck.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private boolean status;
    private int http;
    private String[] msg;
}
