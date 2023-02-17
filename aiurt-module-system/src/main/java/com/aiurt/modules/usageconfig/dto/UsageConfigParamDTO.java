package com.aiurt.modules.usageconfig.dto;

import lombok.Data;

/**
 * @author zwl
 */
@Data
public class UsageConfigParamDTO {


    private String usageConfigParamId;

    private String tableName;

    private String staCondition;

    private String state;

    private String startTime;

    private String endTime;

    private Integer sign;
}
