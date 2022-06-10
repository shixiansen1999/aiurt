package com.aiurt.boot.modules.message.util;

public enum WxTemplateEnum {

    APPROVAL_TEMPLATE_CODE(1, "Y2XyyIaeTYDGNukyOsCY2YFJ2f38rr7VExYMARbLyaM", "业务审批提醒", "first.DATA,keyword1.DATA"),
    ALERT_TEMPLATE_CODE(2, "lgZF_1Uwx767cyhGXVNTARqeYSv7HNczqSXcmcBnQYI", "文件提醒查看通知", "first.DATA,keyword1.DATA");


    private Integer code;

    /**
     * 短信模板编码
     */
    private String templateCode;
    /**
     * 签名
     */
    private String name;
    /**
     * 短信模板必需的数据名称，多个param以逗号分隔，此处配置作为校验
     */
    private String params;

    WxTemplateEnum(Integer code, String templateCode, String name, String params) {
        this.code = code;
        this.templateCode = templateCode;
        this.name = name;
        this.params = params;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public static WxTemplateEnum toEnum(Integer code) {
        if (code == null) {
            return WxTemplateEnum.APPROVAL_TEMPLATE_CODE;
        }
        for (WxTemplateEnum item : WxTemplateEnum.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

