package com.aiurt.boot.plan.rep;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wgp
 * @Title:
 * @Description: 检修计划详情请求参数
 * @date 2022/6/2310:41
 */
@Data
public class RepairStrategyReq {
    @ApiParam(name="id",required=true,value = "检修计划id")
    @NotBlank(message = "检修计划id不能为空")
    private String id;
    @ApiParam(name="id",required=false,value = "专业code")
    private String majorCode;
    @ApiParam(name="id",required=false,value = "专业子系统code")
    private String subSystemCode;
    @ApiParam(name="id",required=true,value = "检修标准code")
    @NotBlank(message = "检修标准code不能为空")
    private String code;
}
