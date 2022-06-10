package com.aiurt.common.api.vo;

public enum I18nErrorEnum {

    //通用成功返回
    CODE0(0, "成功", "success"),
    //通用错误返回
    ERROR400(400, "失败", "failure");

    private int code;
    private String cnMsg;
    private String enMsg;

    I18nErrorEnum(int code, String cnMsg, String enMsg) {
        this.code = code;
        this.cnMsg = cnMsg;
        this.enMsg = enMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCnMsg() {
        return cnMsg;
    }

    public void setCnMsg(String cnMsg) {
        this.cnMsg = cnMsg;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }
}
