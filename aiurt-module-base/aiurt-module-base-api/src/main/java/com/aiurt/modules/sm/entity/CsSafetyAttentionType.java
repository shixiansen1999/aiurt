package com.aiurt.modules.sm.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 安全事项类型表
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
@Data
@TableName("cs_safety_attention_type")
@ApiModel(value="cs_safety_attention_type对象", description="安全事项类型表")
public class CsSafetyAttentionType extends DictEntity implements Serializable {
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
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private java.lang.String sysOrgCode;
	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码",required = true)
    @NotBlank(message = "专业编码不能为空")
    @Size(max = 32, message = "专业编码长度不能超过32个字符")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private java.lang.String majorCode;
	/**安全事项分类编码*/
	@Excel(name = "安全事项分类编码", width = 15)
    @ApiModelProperty(value = "安全事项分类编码",required = true)
    @NotBlank(message = "安全事项分类编码不能为空")
    @Size(max = 32, message = "安全事项分类编码长度不能超过32个字符")
    private java.lang.String code;
	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称",required = true)
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    private java.lang.String name;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序",required = true)
    private java.lang.String sort;
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private java.lang.String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;
	/**编码层级（如/a/ab/abc/）*/
	@Excel(name = "编码层级（如/a/ab/abc/）", width = 15)
    @ApiModelProperty(value = "编码层级（如/a/ab/abc/）")
    private java.lang.String codeScc;
	/**删除标记 0，未删除，1已删除*/
	@Excel(name = "删除标记 0，未删除，1已删除", width = 15)
    @ApiModelProperty(value = "删除标记 0，未删除，1已删除")
    @TableLogic(value = "0", delval = "1")
    private java.lang.Integer delFlag;
}
