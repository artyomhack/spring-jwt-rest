package com.admin.reactive.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class UsernamePasswordAuthenticationException {
    private int status;
    private String message;
    private Date timeStamp;

    public UsernamePasswordAuthenticationException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timeStamp = new Date();
    }
}
