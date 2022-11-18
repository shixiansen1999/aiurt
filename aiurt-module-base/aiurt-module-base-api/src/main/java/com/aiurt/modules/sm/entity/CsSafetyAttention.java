package com.aiurt.modules.sm.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private java.lang.String majorCode;
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
	@Excel(name = "安全事项内容", width = 15)
    @ApiModelProperty(value = "安全事项内容")
    private java.lang.String attentionContent;
	/**安全事项措施*/
	@Excel(name = "安全事项措施", width = 15)
    @ApiModelProperty(value = "安全事项措施")
    private java.lang.String attentionMeasures;
	/**事项状态（0，1启用）*/
	@Excel(name = "事项状态（0，1启用）", width = 15, dicCode = "event _status")
	@Dict(dicCode = "event _status")
    @ApiModelProperty(value = "事项状态（0，1启用）")
    private java.lang.Integer state;
    @Excel(name = "事项状态", width = 15)
    @TableField(exist = false)
    private java.lang.String stateName;
	/**删除标记 0，未删除，1已删除*/
	@Excel(name = "删除标记 0，未删除，1已删除", width = 15)
    @ApiModelProperty(value = "删除标记 0，未删除，1已删除")
    private java.lang.Integer delFlag;
}
