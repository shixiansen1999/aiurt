package com.aiurt.boot.team.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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

    /**
     * 新增保存时的校验分组
     */
    public interface Save {}

    /**
     * 修改时的校验分组
     */
    public interface Update {}
	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**训练计划id*/
    @Excel(name = "训练计划id", width = 15)
    @ApiModelProperty(value = "训练计划id")
    @NotBlank(message = "训练计划id不能为空" , groups = Save.class)
    private String emergencyTrainingProgramId;
	/**应急队伍id*/
	@Excel(name = "应急队伍id", width = 15)
    @ApiModelProperty(value = "应急队伍id")
    @NotBlank(message = "应急队伍id不能为空", groups = {Save.class, Update.class})
    private String emergencyTeamId;
	/**训练时间*/
	@Excel(name = "训练时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "训练时间")
    @NotNull(message = "训练时间不能为空", groups = {Save.class, Update.class})
    private java.util.Date trainingTime;
	/**参加人数*/
	@Excel(name = "训练人数", width = 15)
    @ApiModelProperty(value = "训练人数")
    @NotNull(message = "训练人数不能为空", groups = {Save.class, Update.class})
    private Integer traineesNum;
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
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
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

    @ApiModelProperty(value = "参训人员")
    @TableField(exist = false)
    @Valid
    private List<EmergencyTrainingRecordCrew> crewList;

    @ApiModelProperty(value = "记录附件")
    @TableField(exist = false)
    @Valid
    private List<EmergencyTrainingRecordAtt> attList;

    @ApiModelProperty(value = "训练过程记录")
    @TableField(exist = false)
    @Valid
    private List<EmergencyTrainingProcessRecord> processRecordList;
}
