package com.aiurt.boot.plan.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
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
 * @Description: emergency_plan_att
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_plan_att")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_plan_att对象", description="emergency_plan_att")
public class EmergencyPlanAtt extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**应急预案id*/
	@Excel(name = "应急预案id", width = 15)
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;
	/**附件路径*/
	@Excel(name = "附件路径", width = 15)
    @ApiModelProperty(value = "附件路径")
    private String path;
	/**附件名称*/
	@Excel(name = "附件名称", width = 15)
    @ApiModelProperty(value = "附件名称")
    private String name;
	/**附件大小*/
	@Excel(name = "附件大小", width = 15)
    @ApiModelProperty(value = "附件大小")
    private String size;
	/**附件类型*/
	@Excel(name = "附件类型", width = 15)
    @ApiModelProperty(value = "附件类型")
    private String type;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText ="realname")
    private String createBy;
}
