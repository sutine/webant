package org.webant.commons.http;

import java.util.HashMap;

public class HttpDataResponse<T> {

    private int code = 0;

    private String msg = "success";

    private T data;

    public HttpDataResponse() {
    }

    public HttpDataResponse(T data) {
        this.data = data;
    }

    public HttpDataResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static HttpDataResponse<?> success() {
        return new HttpDataResponse<>(new HashMap<String, Object>());
    }

    public static HttpDataResponse<?> success(Object data) {
        return new HttpDataResponse<>(data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public boolean isFailure() {
        return code != 0;
    }
}
