package com.aiurt.boot.task.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: patrol_task_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_task_standard")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_task_standard对象", description="patrol_task_standard")
public class PatrolTaskStandard implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**巡检任务表ID*/
	@Excel(name = "巡检任务表ID", width = 15)
    @ApiModelProperty(value = "巡检任务表ID")
    private java.lang.String taskId;
    /**巡检标准表ID*/
    @Excel(name = "巡检标准表ID", width = 15)
    @ApiModelProperty(value = "巡检标准表ID")
    private java.lang.String standardId;
	/**标准编号*/
	@Excel(name = "标准编号", width = 15)
    @ApiModelProperty(value = "标准编号")
    private java.lang.String standardCode;
	/**专业code*/
	@Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private java.lang.String professionCode;
	/**系统code*/
	@Excel(name = "系统code", width = 15)
    @ApiModelProperty(value = "系统code")
    private java.lang.String subsystemCode;
	/**设备类型code*/
	@Excel(name = "设备类型code", width = 15)
    @ApiModelProperty(value = "设备类型code")
    private java.lang.String deviceTypeCode;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
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
