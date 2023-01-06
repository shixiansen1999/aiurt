package com.aiurt.boot.team.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @author LKJ
 */
@Data
public class EmergencyTrainingRecordDTO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**应急队伍id*/
    @Excel(name = "应急队伍id", width = 15)
    @ApiModelProperty(value = "应急队伍id")
    private String emergencyTeamId;

    /**训练项目名称*/
    @Excel(name = "训练项目名称", width = 15)
    @ApiModelProperty(value = "训练项目名称")
    private String trainingProgramName;

    /**训练开始时间*/
    @Excel(name = "训练开始时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "训练开始时间")
    private java.util.Date trainingStartTime;

    /**训练结束时间*/
    @Excel(name = "训练结束时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "训练结束时间")
    private java.util.Date trainingEndTime;

    /**训练计划编号*/
    @Excel(name = "训练计划编号", width = 15)
    @ApiModelProperty(value = "训练计划编号")
    private String trainingProgramCode;


    @Excel(name = "应急队伍ids", width = 15)
    @ApiModelProperty(value = "应急队伍ids")
    private List<String> ids;
}
