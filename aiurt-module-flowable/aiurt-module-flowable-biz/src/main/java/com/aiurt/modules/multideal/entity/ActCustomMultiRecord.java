package com.aiurt.modules.multideal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 多实例加、减签记录
 * @Author: aiurt
 * @Date:   2023-09-25
 * @Version: V1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("act_custom_multi_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_multi_record对象", description="多实例加、减签记录")
public class ActCustomMultiRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private java.lang.String taskId;
	/**实例id*/
	@Excel(name = "实例id", width = 15)
    @ApiModelProperty(value = "实例id")
    private java.lang.String processInstanceId;
	/**操作者*/
	@Excel(name = "操作者", width = 15)
    @ApiModelProperty(value = "操作者")
    private java.lang.String userName;
	/**加签/减签用户*/
	@Excel(name = "加签/减签用户", width = 15)
    @ApiModelProperty(value = "加签/减签用户")
    private java.lang.String mutilUserName;
	/**类型（1：加签，2:减钱）*/
	@Excel(name = "类型（1：加签，2:减钱）", width = 15)
    @ApiModelProperty(value = "类型（1：加签，2:减钱）")
    private java.lang.Integer mutilType;
	/**执行实例id*/
	@Excel(name = "执行实例id", width = 15)
    @ApiModelProperty(value = "执行实例id")
    private java.lang.String executionId;
	/**0-未删除，1已删除（减签）*/
	@Excel(name = "0-未删除，1已删除（减签）", width = 15)
    @ApiModelProperty(value = "0-未删除，1已删除（减签）")
    @TableLogic
    private java.lang.Integer delFlag;
	/**节点id*/
	@Excel(name = "节点id", width = 15)
    @ApiModelProperty(value = "节点id")
    private java.lang.String nodeId;
	/**理由*/
	@Excel(name = "理由", width = 15)
    @ApiModelProperty(value = "理由")
    private java.lang.String reason;

    @ApiModelProperty(value = "父执行实例id")
	private String parentExecutionId;
}
