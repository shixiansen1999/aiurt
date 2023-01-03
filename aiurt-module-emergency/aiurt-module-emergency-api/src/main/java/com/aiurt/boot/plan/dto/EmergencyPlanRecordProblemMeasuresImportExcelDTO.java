package com.aiurt.boot.plan.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: emergency_plan_record_problem_measures
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyPlanRecordProblemMeasuresImportExcelDTO implements Serializable {

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**应急预案启动记录id*/
	@Excel(name = "应急预案启动记录id", width = 15)
    @ApiModelProperty(value = "应急预案启动记录id")
    private String emergencyPlanRecordId;
	/**问题类型*/
	@Excel(name = "问题类型", width = 15)
    @ApiModelProperty(value = "问题类型")
    private String problemType;
	/**问题描述*/
	@Excel(name = "问题描述", width = 15)
    @ApiModelProperty(value = "问题描述")
    private String problemContent;
	/**责任部门*/
	@Excel(name = "责任部门", width = 15)
    @ApiModelProperty(value = "责任部门")
    private String orgCode;


    /**责任部门负责人ID*/
    @Excel(name = "责任部门负责人ID", width = 15)
    @ApiModelProperty(value = "责任部门负责人ID")
    private String orgUserId;

	/**负责人id*/
	@Excel(name = "负责人id", width = 15)
    @ApiModelProperty(value = "负责人id")
    private String managerId;


	/**解决期限*/
	@Excel(name = "解决期限", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "解决期限")
    private String resolveTime;

	/**问题状态（1待处理、2已处理）*/
	@Excel(name = "问题状态（1待处理、2已处理）", width = 15)
    @ApiModelProperty(value = "问题状态（1待处理、2已处理）")
    @Dict(dicCode = "emergency_problem_status")
    private String  status;

    /**
     * 问题及措施错误原因
     */
    @ApiModelProperty(value = "问题及措施错误原因")
    @TableField(exist = false)
    private String errorReason;

}
