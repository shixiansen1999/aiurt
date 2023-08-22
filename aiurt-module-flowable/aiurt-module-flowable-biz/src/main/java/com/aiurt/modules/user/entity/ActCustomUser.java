package com.aiurt.modules.user.entity;

import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
import java.util.List;

/**
 * @Description: 流程办理人与抄送人
 * @Author: aiurt
 * @Date:   2023-07-25
 * @Version: V1.0
 */
@Data
@TableName(value = "act_custom_user", autoResultMap = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_user对象", description="流程办理人与抄送人")
public class ActCustomUser implements Serializable {
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
	/**0-未删除，1已删除*/
	@Excel(name = "0-未删除，1已删除", width = 15)
    @ApiModelProperty(value = "0-未删除，1已删除")
    @TableLogic
    private Integer delFlag;
	/**流程引擎的定义Id*/
	@Excel(name = "流程引擎的定义Id", width = 15)
    @ApiModelProperty(value = "流程引擎的定义Id")
    private String processDefinitionId;
	/**任务定义id*/
	@Excel(name = "任务定义id", width = 15)
    @ApiModelProperty(value = "任务定义id")
    private String taskId;
	/**用户账号*/
	@Excel(name = "用户账号", width = 15)
    @ApiModelProperty(value = "用户账号")
    private String userName;
	/**机构id*/
	@Excel(name = "机构id", width = 15)
    @ApiModelProperty(value = "机构id")
    private String orgId;
	/**岗位字典值*/
	@Excel(name = "岗位字典值", width = 15)
    @ApiModelProperty(value = "岗位字典值")
    private String post;
	/**角色编码*/
	@Excel(name = "角色编码", width = 15)
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    /**类型，0：办理人，1：抄送人*/
	@Excel(name = "类型，0：办理人，1：抄送人", width = 15)
    @ApiModelProperty(value = "类型，0：办理人，1：抄送人")
    private String type;

	@ApiModelProperty(value = "关系")
    @TableField(typeHandler = JacksonTypeHandler.class)
	private JSONArray relation;
}
