package com.aiurt.boot.team.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author lkj
 */
@Data
public class EmergencyTrainingProgramDTO {
    private static final long serialVersionUID = 1L;

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**训练计划编号*/
    @Excel(name = "训练计划编号", width = 15)
    @ApiModelProperty(value = "训练计划编号")
    private String trainingProgramCode;
    /**训练项目名称*/
    @Excel(name = "训练项目名称", width = 15)
    @ApiModelProperty(value = "训练项目名称")
    private String trainingProgramName;
    /**计划训练时间*/
    @Excel(name = "计划训练时间", width = 15, format = "yyyy-MM")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM")
    @DateTimeFormat(pattern="yyyy-MM")
    @ApiModelProperty(value = "计划训练时间")
    private java.util.Date trainingPlanTime;
    /**计划训练人数*/
    @Excel(name = "计划训练人数", width = 15)
    @ApiModelProperty(value = "计划训练人数")
    private Integer traineesNum;
    /**审核状态（1待提交、2待完成、3已完成）*/
    @Excel(name = "审核状态（1待下发、2待完成、3已完成）", width = 15)
    @ApiModelProperty(value = "审核状态（1待下发、2待完成、3已完成）")
    private Integer status;
    /**编制部门*/
    @Excel(name = "编制部门", width = 15)
    @ApiModelProperty(value = "编制部门")
    private String orgCode;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**删除状态： 0未删除 1已删除*/
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
}
