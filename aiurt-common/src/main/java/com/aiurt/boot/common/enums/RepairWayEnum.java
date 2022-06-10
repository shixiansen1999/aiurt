package com.aiurt.boot.common.enums;

/**
 * @Author WangHongTao
 * @Date 2021/11/26
 */
public enum  RepairWayEnum {
    ZJ(1, "自检"),
    BX(2, "报修");

    private int code;
    private String message;

    RepairWayEnum(int code, String message){
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
