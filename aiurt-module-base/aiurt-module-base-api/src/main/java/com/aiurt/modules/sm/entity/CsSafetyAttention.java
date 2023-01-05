package com.aiurt.modules.sm.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: 安全事项
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Data
@TableName("cs_safety_attention")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_safety_attention对象", description="安全事项")
public class CsSafetyAttention implements Serializable {
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
	/**专业编码*/
	@Excel(name = "专业名称", width = 15,dictTable = "cs_major", dicText = "major_name",dicCode = "major_code")
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private java.lang.String majorCode;
    /**子系统编码*/
    @Excel(name = "子系统", width = 15,dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @ApiModelProperty(value = "子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private java.lang.String systemCode;

    @Excel(name = "专业子系统", width = 15)
    @TableField(exist = false)
    private java.lang.String systemName;
    /**专业编码*/
    @Excel(name = "专业", width = 15)
    @ApiModelProperty(value = "专业")
    @TableField(exist = false)
    private java.lang.String majorName;
	/**事项分类编码*/
	@Excel(name = "事项分类编码", width = 15)
    @ApiModelProperty(value = "事项分类编码")
    private java.lang.String attentionTypeCode;
	/**事项分类id*/
	@Excel(name = "事项分类id", width = 15)
    @ApiModelProperty(value = "事项分类id")
    private java.lang.String attentionType;
    /**事项分类id*/
    @Excel(name = "安全事项分类", width = 15)
    @ApiModelProperty(value = "安全事项分类")
    @TableField(exist = false)
    private java.lang.String attentionTypeName;
	/**安全事项内容*/
	@Excel(name = "安全事项内容和措施", width = 75)
    @ApiModelProperty(value = "安全事项内容和措施")
    private java.lang.String attentionContent;
	/**安全事项摘要*/
	@Excel(name = "安全事项摘要", width = 15)
    @ApiModelProperty(value = "安全事项摘要")
    private java.lang.String attentionMeasures;
	/**事项状态（0，1启用）*/
	@Excel(name = "事项状态", width = 15, dicCode = "event _status")
	@Dict(dicCode = "event _status")
    @ApiModelProperty(value = "事项状态")
    private java.lang.Integer state;
    @Excel(name = "事项状态", width = 15)
    @TableField(exist = false)
    private java.lang.String stateName;
	/**删除标记 0，未删除，1已删除*/
	@Excel(name = "删除标记 0，未删除，1已删除", width = 15)
    @ApiModelProperty(value = "删除标记 0，未删除，1已删除")
    private java.lang.Integer delFlag;
    /**删除标记 0，未删除，1已删除*/
    @Excel(name = "错误原因", width = 15)
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private java.lang.String text;
    @TableField(exist = false)
    private  String ids;
}
