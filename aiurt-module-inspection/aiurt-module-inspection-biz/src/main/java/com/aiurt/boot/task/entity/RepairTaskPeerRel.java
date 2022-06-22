package com.aiurt.boot.task.entity;

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
 * @Description: repair_task_peer_rel
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_task_peer_rel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_task_peer_rel对象", description="repair_task_peer_rel")
public class RepairTaskPeerRel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**关联repair_task_device_rel表的code*/
	@Excel(name = "关联repair_task_device_rel表的code", width = 15)
    @ApiModelProperty(value = "关联repair_task_device_rel表的code")
    private java.lang.String repairTaskDeviceCode;
	/**人员id，关联sys_user的id*/
	@Excel(name = "人员id，关联sys_user的id", width = 15)
    @ApiModelProperty(value = "人员id，关联sys_user的id")
    private java.lang.String userId;
	/**人员名称*/
	@Excel(name = "人员名称", width = 15)
    @ApiModelProperty(value = "人员名称")
    private java.lang.String realName;
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
