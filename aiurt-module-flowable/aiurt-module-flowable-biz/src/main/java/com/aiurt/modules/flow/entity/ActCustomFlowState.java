package com.aiurt.modules.flow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
import java.util.Date;

/**
 * @Description: 流程系统状态表
 * @Author: gaowei
 * @Date:   2023-10-25
 * @Version: V1.0
 */
@Data
@TableName("act_custom_flow_state")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_flow_state对象", description="流程系统状态表")
public class ActCustomFlowState implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**流程实例*/
	@Excel(name = "流程实例", width = 15)
    @ApiModelProperty(value = "流程实例")
    private String processInstanceId;
	/**0-未删除，1已删除*/
	@Excel(name = "0-未删除，1已删除", width = 15)
    @ApiModelProperty(value = "0-未删除，1已删除")
    @TableLogic
    private Integer delFlag;
	/**状态（1待发起，2进行中，3已退回，4已终止，5已作废，6已归档）*/
	@Excel(name = "状态（1待发起，2进行中，3已退回，4已终止，5已作废，6已归档）", width = 15)
    @ApiModelProperty(value = "状态（1待发起，2进行中，3已退回，4已终止，5已作废，6已归档）")
    private Integer state;
}
