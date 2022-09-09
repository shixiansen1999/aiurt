package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-08 9:59
 */
@Data
public class FaultTimeoutLevelReq {
    @ApiModelProperty(value = "开始时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "开始时间不能为空")
    private java.util.Date startTime;

    @ApiModelProperty(value = "结束时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    @ApiModelProperty(value = "故障超时等级:1.一级超时 2.二级超时  3.三级超时")
    @NotNull(message = "超时等级不能为空")
    private Integer level;

    @ApiModelProperty(value = "故障状态")
    private Integer status;

    /**故障级别*/
    @Excel(name = "故障级别", width = 15)
    @ApiModelProperty(value = "故障级别")
    @Dict(dictTable = "fault_level", dicCode = "code", dicText = "name")
    private String faultLevel;

    @ApiModelProperty(value = "维修负责人")
    private String appointUserName;

    @ApiModelProperty(value = "报修方式")
    @Dict(dicCode = "fault_mode_code")
    private String faultModeCode;

    @ApiModelProperty(value = "pageNo")
    private Integer pageNo = 1;
    @ApiModelProperty(value = "pageSize")
    private Integer pageSize = 10;
}
