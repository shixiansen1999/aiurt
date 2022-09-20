package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-08 9:59
 */
@Data
public class FaultCountInfoReq {
    @ApiModelProperty(value = "开始时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "开始时间不能为空")
    private Date startDate;

    @ApiModelProperty(value = "结束时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

    @ApiModelProperty(value = "详情分类:1.故障总数 2.已解决  3.未解决  4.挂起数")
    @NotNull(message = "详情分类不能为空")
    private Integer type;

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
