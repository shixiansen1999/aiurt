package com.aiurt.boot.team.dto;
/**
 * @Description: emergency_team
 * @Author: LKJ
 * @Date:   2022-11-30
 * @Version: V1.0
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class EmergencyTeamDTO {

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**训练项目名称*/
    @Excel(name = "训练项目名称", width = 15)
    @ApiModelProperty(value = "训练项目名称")
    private String trainingProgramName;

    /**训练时间*/
    @Excel(name = "训练时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "训练时间")
    private java.util.Date trainingTime;

    @ApiModelProperty(value = "训练负责人姓名")
    @TableField(exist = false)
    private String managerName;

    /**参加人数*/
    @Excel(name = "训练人数", width = 15)
    @ApiModelProperty(value = "训练人数")
    private Integer traineesNum;

    /**训练计划编号*/
    @Excel(name = "关联训练计划编号", width = 15)
    @ApiModelProperty(value = "关联训练计划编号")
    private String trainingProgramCode;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
}
