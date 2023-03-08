package com.aiurt.modules.param.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: sys_param
 * @Author: aiurt
 * @Date:   2022-12-30
 * @Version: V1.0
 */
@Data
@TableName("sys_param")
@ApiModel(value="sys_param对象", description="sys_param")
public class SysParam implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**参数编号*/
    @ApiModelProperty(value = "参数编号", required = true)
    private String code;
	/**参数名称*/
    @ApiModelProperty(value = "参数名称", required = true)
    private String name;
	/**参数类别*/
    @ApiModelProperty(value = "参数类别")
    private String category;
    /**参数类别*/
    @TableField(exist = false)
    @ApiModelProperty(value = "参数类别名称")
    private String categoryName;
	/**参数值*/
    @ApiModelProperty(value = "参数值")
    private String value;
	/**参数说明*/
    @ApiModelProperty(value = "参数说明")
    private String description;
    /**字典编码*/
    @ApiModelProperty(value = "字典编码")
    private String dictCode ;


	/**删除状态： 0未删除 1已删除*/
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**是否有子节点*/
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    @ApiModelProperty(value = "参数录入方式")
	private String valueMode;
    @ApiModelProperty(value = "子级集合")
    @TableField(exist = false)
    private List<SysParam> children;

    @ApiModelProperty(value = "分类id", required = true)
    @Dict(dicCode = "id", dicText = "type_name", dictTable = "sys_param_type")
    private String paramTypeId;
}
