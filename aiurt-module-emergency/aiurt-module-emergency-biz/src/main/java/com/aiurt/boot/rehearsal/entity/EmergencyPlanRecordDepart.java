package com.aiurt.boot.rehearsal.entity;

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
 * @Description: emergency_plan_record_depart
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan_record_depart")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan_record_depart对象", description="emergency_plan_record_depart")
public class EmergencyPlanRecordDepart implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**应急预案启动记录id*/
	@Excel(name = "应急预案启动记录id", width = 15)
    @ApiModelProperty(value = "应急预案启动记录id")
    private java.lang.String emergencyPlanLaunchRecordId;
	/**参与部门*/
	@Excel(name = "参与部门", width = 15)
    @ApiModelProperty(value = "参与部门")
    private java.lang.String orgCode;
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
