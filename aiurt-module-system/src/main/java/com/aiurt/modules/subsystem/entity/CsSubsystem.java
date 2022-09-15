package com.aiurt.modules.subsystem.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.modules.device.entity.DeviceType;
import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("cs_subsystem")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cs_subsystem对象", description="cs_subsystem")
public class CsSubsystem implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    @TableField(exist = false)
    @ApiModelProperty(value = "key")
    private String key;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String systemName;
    @TableField(exist = false)
    private String label;
    @TableField(exist = false)
    private String value;
	/**编号*/
	@Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
    @SystemFilterColumn
    private String systemCode;
	/**说明*/
	@Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
    private String description;
	/**删除标志，0未删除，1已删除*/
	@Excel(name = "删除标志，0未删除，1已删除", width = 15)
    @ApiModelProperty(value = "删除标志，0未删除，1已删除")
    private Integer delFlag;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**所属专业-专业表*/
	@Excel(name = "所属专业-专业表", width = 15)
    @ApiModelProperty(value = "所属专业-专业表")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @MajorFilterColumn
    private String majorCode;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    private String sysOrgCode;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**子系统人员账号id*/
    @ApiModelProperty(value = "子系统人员账号id")
    @TableField(exist = false)
    private String systemUserList;
    /**子系统人员名称*/
    @ApiModelProperty(value = "子系统人员名称")
    @TableField(exist = false)
    private String systemUserName;
    /**子系统人员*/
    @ApiModelProperty(value = "子系统下的物资分类")
    @TableField(exist = false)
    private List<MaterialBaseType> materialBaseTypeList;

    @ApiModelProperty(value = "备用字段")
    @TableField(exist = false)
    private String byType = "zxt";
    /**与专业关联的子系统*/
    @ApiModelProperty(value = "与专业关联的子系统")
    @TableField(exist = false)
    private List<CsSubsystem> children;
    /**设备类型子集*/
    @Excel(name = "设备类型子集", width = 15)
    @ApiModelProperty(value = "设备类型子集")
    @TableField(exist = false)
    private List<DeviceType> deviceTypeChildren;
}
