package com.acknsyn.brandon.urlwriter.http;

import java.io.IOException;

public class HttpException extends IOException {
    private int status;
    private String response;

    public HttpException(int status, String response) {
        this.status = status;
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public int getStatus() {
        return status;
    }
}
