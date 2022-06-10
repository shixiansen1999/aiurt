package com.aiurt.common.api.vo;

import cn.hutool.json.JSONUtil;

public class ResponseBean<T> {

    private int code;

    private String msg;

    private String sign;

    private T data;

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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    public boolean isSuccess() {
        return code == 0;
    }
}
