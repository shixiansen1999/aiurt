package com.aiurt.modules.team.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_training_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_training_record对象", description="emergency_training_record")
public class EmergencyTrainingRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**训练计划编号*/
	@Excel(name = "训练计划编号", width = 15)
    @ApiModelProperty(value = "训练计划编号")
    private java.lang.String trainingProgramCode;
	/**训练项目名称*/
	@Excel(name = "训练项目名称", width = 15)
    @ApiModelProperty(value = "训练项目名称")
    private java.lang.String trainingProgramName;
	/**应急队伍id*/
	@Excel(name = "应急队伍id", width = 15)
    @ApiModelProperty(value = "应急队伍id")
    private java.lang.String emergencyTeamId;
	/**训练时间*/
	@Excel(name = "训练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "训练时间")
    private java.util.Date trainingTime;
	/**参加人数*/
	@Excel(name = "参加人数", width = 15)
    @ApiModelProperty(value = "参加人数")
    private java.lang.Integer traineesNum;
	/**线路编码*/
	@Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    private java.lang.String lineCode;
	/**站点编码*/
	@Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    private java.lang.String stationCode;
	/**位置编码*/
	@Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    private java.lang.String positionCode;
	/**记录状态（1待提交、2已提交）*/
	@Excel(name = "记录状态（1待提交、2已提交）", width = 15)
    @ApiModelProperty(value = "记录状态（1待提交、2已提交）")
    private java.lang.Integer status;
	/**训练效果评估及改进建议*/
	@Excel(name = "训练效果评估及改进建议", width = 15)
    @ApiModelProperty(value = "训练效果评估及改进建议")
    private java.lang.String trainingAppraise;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
