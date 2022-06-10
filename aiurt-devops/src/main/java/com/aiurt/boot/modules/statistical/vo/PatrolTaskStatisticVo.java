package com.aiurt.boot.modules.statistical.vo;

import lombok.Data;

@Data
public class PatrolTaskStatisticVo {
    private String departName;
    private String departId;
    private Integer planNum;
    private Integer completeNum;
    private Integer ignoreNum;
}
