package org.webant.queen.commons.vo;

import java.util.HashMap;

public class Response<T> {

    private int code = ErrorCode.NONE;

    private String msg = "success";

    private T data;

    public Response() {
    }

    public Response(T data) {
        this.data = data;
    }

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Response<?> success() {
        return new Response<>(new HashMap<String, Object>());
    }

    public static Response<?> success(Object data) {
        return new Response<>(data);
    }

    public static Response<?> failure(int code) {
        return new Response<>(code, ErrorCode.desc(code), new HashMap<String, Object>());
    }

    public static Response<?> failure(int code, String msg) {
        return new Response<>(code, msg, new HashMap<String, Object>());
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
        return code == ErrorCode.NONE;
    }

    public boolean isFailure() {
        return code != ErrorCode.NONE;
    }
}
