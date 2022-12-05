package com.aiurt.boot.team.vo;

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
public class EmergencyTrainingRecordVO {

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
    /**应急队伍id*/
    @Excel(name = "应急队伍id", width = 15)
    @ApiModelProperty(value = "应急队伍id")
    private String emergencyTeamId;
    /**训练时间*/
    @Excel(name = "训练时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "训练时间")
    private java.util.Date trainingTime;
    /**参加人数*/
    @Excel(name = "训练人数", width = 15)
    @ApiModelProperty(value = "训练人数")
    private Integer traineesNum;
    @ApiModelProperty(value = "负责人姓名")
    private String managerName;
    /**线路编码*/
    @Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    private String lineCode;
    /**站点编码*/
    @Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
    /**位置编码*/
    @Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    private String positionCode;
    /**记录状态（1待提交、2已提交）*/
    @Excel(name = "记录状态（1待提交、2已提交）", width = 15)
    @ApiModelProperty(value = "记录状态（1待提交、2已提交）")
    private Integer status;
    /**训练效果评估及改进建议*/
    @Excel(name = "训练效果评估及改进建议", width = 15)
    @ApiModelProperty(value = "训练效果评估及改进建议")
    private String trainingAppraise;
}
