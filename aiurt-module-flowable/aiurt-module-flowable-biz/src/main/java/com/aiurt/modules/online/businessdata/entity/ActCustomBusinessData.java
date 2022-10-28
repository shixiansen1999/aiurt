package com.aiurt.modules.online.businessdata.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 流程中间业务数据
 * @Author: aiurt
 * @Date:   2022-10-27
 * @Version: V1.0
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("act_custom_business_data")
@ApiModel(value="act_custom_business_data对象", description="流程中间业务数据")
public class ActCustomBusinessData implements Serializable {
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
	/**流程定义标识*/
	@Excel(name = "流程定义标识", width = 15)
    @ApiModelProperty(value = "流程定义标识")
    private String processDefinitionKey;
	/**流程名称*/
	@Excel(name = "流程名称", width = 15)
    @ApiModelProperty(value = "流程名称")
    private String processDefinitionName;
	/**流程引擎的定义Id*/
	@Excel(name = "流程引擎的定义Id", width = 15)
    @ApiModelProperty(value = "流程引擎的定义Id")
    private String processDefinitionId;
	/**流程实例Id*/
	@Excel(name = "流程实例Id", width = 15)
    @ApiModelProperty(value = "流程实例Id")
    private String processInstanceId;
	/**在线表单的主表Id*/
	@Excel(name = "在线表单的主表Id", width = 15)
    @ApiModelProperty(value = "在线表单的主表Id")
    private String onlineTableId;
	/**静态表单表名*/
	@Excel(name = "静态表单表名", width = 15)
    @ApiModelProperty(value = "静态表单表名")
    private String tableName;
	/**任务标识*/
	@Excel(name = "任务标识", width = 15)
    @ApiModelProperty(value = "任务标识")
    private String taskDefinitionKey;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taksId;


	// private JSONObject data;
}
