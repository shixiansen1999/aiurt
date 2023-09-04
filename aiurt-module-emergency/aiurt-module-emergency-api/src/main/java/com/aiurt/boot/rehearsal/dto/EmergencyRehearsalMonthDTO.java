package com.aiurt.boot.rehearsal.dto;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应急演练月计划DTO对象
 */
@Data
public class EmergencyRehearsalMonthDTO extends EmergencyRehearsalMonth {
    /**
     * 是否为演练记录接口查询
     */
    @ApiModelProperty(value = "是否为演练记录接口查询")
    private java.lang.Boolean recordInterface;
    /**
     * 部门权限的编码
     */
    @ApiModelProperty(value = "部门权限的编码")
    private List<String> orgCodes;

    @ApiModelProperty(value = "当前登录人部门")
    private java.lang.String userOrgCode;
}
