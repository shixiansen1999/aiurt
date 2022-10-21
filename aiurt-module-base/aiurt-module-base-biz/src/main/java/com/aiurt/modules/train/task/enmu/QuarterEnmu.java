package com.aiurt.modules.train.task.enmu;

/**
 * Administrator
 * 2022/4/22
 * 季度枚举类
 * @author admin
 */
public enum QuarterEnmu {
    /**
     * 第一季度
     * */
    THEFIRSTQUARTER(1, "第一季度"),
    /**
     * 第二季度
     * */
    THESECONDQUARTER(2, "第二季度"),
    /**
     * 第三季度
     * */
    THETHIRDQUARTER(3, "第三季度"),
    /**
     * 第四季度
     * */
    THEFOURTHQUARTER(4, "第四季度");
    private final Integer code;
    private final String desc;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return desc;
    }

    QuarterEnmu(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getByCode(Integer code) {
        QuarterEnmu[] values = QuarterEnmu.values();
        for (QuarterEnmu quarterEnmu : values) {
            if (code.equals(quarterEnmu.getCode())) {
                return quarterEnmu.getName();
            }
        }
        return null;
    }
}
