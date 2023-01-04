package com.aiurt.modules.sm.entity;

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
 * @Description: safety_related_form
 * @Author: aiurt
 * @Date:   2023-01-04
 * @Version: V1.0
 */
@Data
@TableName("safety_related_form")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="safety_related_form对象", description="safety_related_form")
public class SafetyRelatedForm implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**安全事项ID*/
	@Excel(name = "安全事项ID", width = 15)
    @ApiModelProperty(value = "安全事项ID")
    private java.lang.String safetyAttentionId;
	/**巡视标准表code*/
	@Excel(name = "巡视标准表code", width = 15)
    @ApiModelProperty(value = "巡视标准表code")
    private java.lang.String patrolStandardCode;
	/**检修标准表code*/
	@Excel(name = "检修标准表code", width = 15)
    @ApiModelProperty(value = "检修标准表code")
    private java.lang.String inspectionCode;
	/**判断状态: 0巡视,1检修*/
	@Excel(name = "判断状态: 0巡视,1检修", width = 15)
    @ApiModelProperty(value = "判断状态: 0巡视,1检修")
    private java.lang.Integer status;
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
