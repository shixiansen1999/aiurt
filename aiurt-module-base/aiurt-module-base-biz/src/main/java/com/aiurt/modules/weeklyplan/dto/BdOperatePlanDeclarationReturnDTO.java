package com.aiurt.modules.weeklyplan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class BdOperatePlanDeclarationReturnDTO {

    @Excel(name = "作业范围", width = 30)
    @ApiModelProperty(value = "作业范围")
    private String taskRange;

    @Excel(name = "作业内容", width = 50)
    @ApiModelProperty(value = "作业内容")
    private String taskContent;

    @Excel(name = "施工负责人", width = 15)
    @ApiModelProperty(value = "施工负责人")
    private String chargeStaffName;

    @Excel(name = "施工负责人id", width = 15)
    @ApiModelProperty(value = "施工负责人id")
    private String chargeStaffId;

}
