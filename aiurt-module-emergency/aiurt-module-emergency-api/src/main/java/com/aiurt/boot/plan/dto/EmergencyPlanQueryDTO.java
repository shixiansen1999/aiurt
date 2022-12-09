package com.aiurt.boot.plan.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/5
 * @time: 15:34
 */

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
public class EmergencyPlanQueryDTO {
    /**
     * 应急预案名称
     */
    @ApiModelProperty(value = "应急预案名称")
    private String emergencyPlanName;
    /**
     * 应急预案类型
     */
    @ApiModelProperty(value = "应急预案类型")
    private Integer emergencyPlanType;

    @ApiModelProperty(value = "应急预案状态")
    private Integer status;

    /**
     * 组织机构编码
     */
    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;
}
