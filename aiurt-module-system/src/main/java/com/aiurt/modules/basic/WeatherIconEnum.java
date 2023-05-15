package com.aiurt.modules.basic;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.fault.enums.FaultStatusEnum;

public enum WeatherIconEnum {

    CODE_100(100, "晴"),
    CODE_101(101, "多云"),
    CODE_102(102, "少云"),
    CODE_103(103, "晴间多云"),
    CODE_104(104, "阴"),
    CODE_150(150, "晴"),
    CODE_151(151, "多云"),
    CODE_152(152, "少云"),
    CODE_153(153, "晴间多云"),
    CODE_300(300, "阵雨"),
    CODE_301(301, "强阵雨"),
    CODE_302(302, "雷阵雨"),
    CODE_303(303, "强雷阵雨"),
    CODE_304(304, "雷阵雨伴有冰雹"),
    CODE_305(305, "小雨"),
    CODE_306(306, "中雨"),
    CODE_307(307, "大雨"),
    CODE_308(308, "极端降雨"),
    CODE_309(309, "毛毛雨/细雨"),
    CODE_310(310, "暴雨"),
    CODE_311(311, "大暴雨"),
    CODE_312(312, "特大暴雨"),
    CODE_313(313, "冻雨"),
    CODE_314(314, "小到中雨"),
    CODE_315(315, "中到大雨"),
    CODE_316(316, "大到暴雨"),
    CODE_317(317, "暴雨到大暴雨"),
    CODE_318(318, "大暴雨到特大暴雨"),
    CODE_350(350, "阵雨"),
    CODE_351(351, "强阵雨"),
    CODE_399(399, "雨"),
    CODE_400(400, "小雪"),
    CODE_401(401, "中雪"),
    CODE_402(402, "大雪"),
    CODE_403(403, "暴雪"),
    CODE_404(404, "雨夹雪"),
    CODE_405(405, "雨雪天气"),
    CODE_406(406, "阵雨夹雪"),
    CODE_407(407, "阵雪"),
    CODE_408(408, "小到中雪"),
    CODE_409(409, "中到大雪"),
    CODE_410(410, "大到暴雪"),
    CODE_456(456, "阵雨夹雪"),
    CODE_457(457, "阵雪"),
    CODE_499(499, "雪"),
    CODE_500(500, "薄雾"),
    CODE_501(501, "雾"),
    CODE_502(502, "霾"),
    CODE_503(503, "扬沙"),
    CODE_504(504, "浮尘"),
    CODE_507(507, "沙尘暴"),
    CODE_509(509, "浓雾"),
    CODE_508(508, "强沙尘暴"),
    CODE_510(510, "强浓雾"),
    CODE_511(511, "中度霾"),
    CODE_512(512, "重度霾"),
    CODE_513(513, "严重霾"),
    CODE_514(514, "大雾"),
    CODE_515(515, "特强浓雾"),
    CODE_900(900, "热"),
    CODE_901(901, "冷"),
    CODE_999(999, "未知");



    private Integer code;
    private String detail;

    private WeatherIconEnum(Integer code, String detail){
        this.detail = detail;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }



    public String getDetail() {
        return detail;
    }




    public static WeatherIconEnum getByCode(String detail) {
        for (WeatherIconEnum faultStatusEnum : WeatherIconEnum.values()) {
            if (StrUtil.equalsIgnoreCase(detail, faultStatusEnum.getDetail())) {
                return faultStatusEnum;
            }
        }
        return null;
    }
}
