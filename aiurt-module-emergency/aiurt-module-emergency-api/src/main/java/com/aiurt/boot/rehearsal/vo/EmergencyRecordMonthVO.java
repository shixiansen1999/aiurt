package com.aiurt.boot.rehearsal.vo;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author
 * @date 2022/12/2 11:42
 * @description: 应急实施记录月计划关联信息VO对象
 */
@Data
@ApiModel(value = "应急实施记录月计划关联信息VO对象", description = "应急实施记录月计划关联信息VO对象")
public class EmergencyRecordMonthVO extends EmergencyRehearsalMonth {
    /**
     * 年度
     */
    @ApiModelProperty(value = "年度")
    private String year;
    /**
     * 演练类型字典名称
     */
    @ApiModelProperty(value = "演练类型字典名称")
    private String typeName;
    /**
     * 计划类型字典名称
     */
    @ApiModelProperty(value = "计划类型字典名称")
    private String yearWithinName;

    /**
     * 演练形式字典名称
     */
    @ApiModelProperty(value = "演练形式字典名称")
    private String modalityName;
    /**
     * 组织部门名称
     */
    @ApiModelProperty(value = "组织部门名称")
    private String orgCodeName;
}
