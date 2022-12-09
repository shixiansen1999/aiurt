package com.aiurt.boot.plan.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/5
 * @time: 15:34
 */

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-05 15:34
 */
@Data
public class EmergencyPlanRecordQueryDTO {
    /**
     * 事件类型
     */
    @ApiModelProperty(value = "事件类型")
    private Integer eventClass;
    /**
     * 启动应急预案
     */
    @ApiModelProperty(value = "启动应急预案")
    private String emergencyPlanId;

    @ApiModelProperty(value = "应急预案启动记录id")
    private String emergencyPlanRecordId;
    /**
     * 开始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    /**
     * 结束日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;
    /**
     * 组织机构编码
     */
    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;
}
