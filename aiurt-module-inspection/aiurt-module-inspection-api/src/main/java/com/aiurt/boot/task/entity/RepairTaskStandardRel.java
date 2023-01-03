package com.aiurt.boot.task.entity;

import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
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

import java.io.Serializable;

/**
 * @Description: repair_task_standard_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_task_standard_rel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_task_standard_rel对象", description="repair_task_standard_rel")
public class RepairTaskStandardRel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**检修任务表ID,关联repair_task的id*/
	@Excel(name = "检修任务表ID,关联repair_task的id", width = 15)
    @ApiModelProperty(value = "检修任务表ID,关联repair_task的id")
    private java.lang.String repairTaskId;
	/**检修标准名称*/
	@Excel(name = "检修标准名称", width = 15)
    @ApiModelProperty(value = "检修标准名称")
    private java.lang.String title;
	/**检修标准编码，关联inspection_code的code*/
	@Excel(name = "检修标准编码，关联inspection_code的code", width = 15)
    @ApiModelProperty(value = "检修标准编码，关联inspection_code的code")
    private java.lang.String code;
    /**是否与设备类型相关(0否1是)*/
    @Excel(name = "是否与设备类型相关(0否1是)", width = 15)
    @ApiModelProperty(value = "是否与设备类型相关(0否1是)")
    private java.lang.Integer isAppointDevice;
	/**专业code,关联cs_major的code*/
	@Excel(name = "专业code,关联cs_major的code", width = 15)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    @MajorFilterColumn
    private java.lang.String majorCode;
	/**专业子系统code,关联cs_subsystem_user的code*/
	@Excel(name = "专业子系统code,关联cs_subsystem_user的code", width = 15)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    @SystemFilterColumn
    private java.lang.String subsystemCode;
	/**设备类型code，关联device_type的code*/
	@Excel(name = "设备类型code，关联device_type的code", width = 15)
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    private java.lang.String deviceTypeCode;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
