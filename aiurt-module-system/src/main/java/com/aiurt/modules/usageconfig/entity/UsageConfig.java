package com.aiurt.modules.usageconfig.entity;

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
import java.util.Date;
import java.util.List;

/**
 * @Description: work_area
 * @Author: aiurt
 * @Date:   2022-08-11
 * @Version: V1.0
 */
@Data
@TableName("sys_usage_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_usage_config对象", description="sys_usage_config")
public class UsageConfig implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private String pid;
	/**是否有子节点（0否1是）*/
	@Excel(name = "是否有子节点（0否1是）", width = 15)
    @ApiModelProperty(value = "是否有子节点（0否1是）")
    private Integer hasChild;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String name;
	/**统计表名（只能是英文）*/
	@Excel(name = "统计表名（只能是英文）", width = 15)
    @ApiModelProperty(value = "统计表名（只能是英文）")
    private String tableName;
	/**统计条件*/
	@Excel(name = "统计条件", width = 15)
    @ApiModelProperty(value = "统计条件")
    private String staCondition;
	@Excel(name = "状态（1启用/0禁用)", width = 15)
    @ApiModelProperty(value = "状态（1启用/0禁用)")
    private Integer state;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "总数")
    private Integer total;

    @TableField(exist = false)
    @ApiModelProperty(value = "新增数")
    private Integer newlyAdded;

    @TableField(exist = false)
    @ApiModelProperty(value = "子级")
    private List<UsageConfig> children;
}
