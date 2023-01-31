package com.aiurt.boot.category.entity;

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
 * @Description: fixed_assets_category
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
@TableName("fixed_assets_category")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fixed_assets_category对象", description="fixed_assets_category")
public class FixedAssetsCategory implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**父级ID，第一级默认0*/
	@Excel(name = "父级ID，第一级默认0", width = 15)
    @ApiModelProperty(value = "父级ID，第一级默认0")
    private java.lang.String pid;
    /**父级ID，第一级默认0*/
    @Excel(name = "父级Code", width = 15)
    @ApiModelProperty(value = "父级code")
    @TableField(exist = false)
    private java.lang.String parentCode;
	/**层级*/
	@Excel(name = "层级", width = 15)
    @ApiModelProperty(value = "层级")
    private java.lang.String level;
	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private java.lang.String categoryName;
	/**分类编码*/
	@Excel(name = "分类编码", width = 15)
    @ApiModelProperty(value = "分类编码")
    private java.lang.String categoryCode;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**上级节点*/
    @Excel(name = "上级节点", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "上级节点")
    private java.lang.String pidName;
    private java.lang.Integer delFlag;
    /**是否已经导入上级节点*/
    @Excel(name = "是否已经导入上级节点", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "是否已经导入上级节点")
    private java.lang.Boolean isNotImportParentNode;
    /**创建人*/
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date createTime;
	/**更新人*/
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private java.util.Date updateTime;
}
