package com.aiurt.modules.device.entity;

import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
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
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("device_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="device_type对象", description="device_type")
public class DeviceType implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**所属专业*/
    @Excel(name = "专业名称", width = 15,dictTable = "cs_major", dicText = "major_name",dicCode = "major_code")
    @ApiModelProperty(value = "所属专业")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @MajorFilterColumn
    private String majorCode;
    /**专业名称*/
    @Excel(name = "专业", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    @Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
    private String majorName;
	/**系统编号*/
    @Excel(name = "子系统", width = 15,dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @ApiModelProperty(value = "系统编号")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String systemCode;
    /**系统名称*/
    @Excel(name = "子系统", width = 15,dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @ApiModelProperty(value = "系统名称")
    @TableField(exist = false)
    private String systemName;
	/**分类编号*/
	@Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
    private String code;
    /**分类数值*/
    @Excel(name = "分类数值", width = 15)
    @ApiModelProperty(value = "分类数值")
    @TableField(exist = false)
    private String value;
	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private String name;
    /**分类标题*/
    @Excel(name = "分类标题", width = 15)
    @ApiModelProperty(value = "分类标题")
    @TableField(exist = false)
    private String title;
	/**状态 0-停用 1-正常*/
	@Excel(name = "状态 0-停用 1-正常", width = 15,dicCode = "device_type_status")
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    @Dict(dicCode = "device_type_status")
    private Integer status;
    @Excel(name = "分类状态", width = 15)
    @TableField(exist = false)
	String statusName;
	/**是否为特种设备(1:是,0:否)*/
    @ApiModelProperty(value = "是否为特种设备(1:是,0:否)")
    @Dict(dicCode = "is_special_device")
    private Integer isSpecialDevice;
    @Excel(name = "是否特种设备", width = 15)
    @TableField(exist = false)
	String isSpecialDeviceName;
	/**是否尾节点(1:是,0：否)*/
    @ApiModelProperty(value = "是否尾节点(1:是,0：否)")
    @Dict(dicCode = "is_end")
    private Integer isEnd;
    @Excel(name = "是否尾节点", width = 15)
    @TableField(exist = false)
	String isEndName;
	/**层级结构*/
    @ApiModelProperty(value = "层级结构")
    private String codeCc;
	/**上级节点*/
    @ApiModelProperty(value = "上级节点")
    private String pid;
    /**上级节点-名称*/
    @Excel(name = "上级节点-名称", width = 15)
    @ApiModelProperty(value = "上级节点-名称")
    @TableField(exist = false)
    private String pUrl;
	/**删除状态 0-未删除 1-已删除*/
	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**设备类型子集*/
    @ApiModelProperty(value = "设备类型子集")
    @TableField(exist = false)
    private List<DeviceType> children;
    /**设备组成*/
    @ExcelCollection(name = "设备组成对应的物资编码")
    @ApiModelProperty(value = "设备组成")
    @TableField(exist = false)
    private List<DeviceCompose> deviceComposeList;
    /**节点类型*/
    @Excel(name = "节点类型", width = 15)
    @ApiModelProperty(value = "节点类型")
    @TableField(exist = false)
    private String treeType;
    /**是否有设备组成*/
    @Excel(name = "是否有设备组成", width = 15)
    @ApiModelProperty(value = "是否有设备组成")
    @TableField(exist = false)
    @Dict(dicCode = "is_have_device")
    private Integer isHaveDevice;
    /**父节点是否是特种设备*/
    @Excel(name = "父节点是否是特种设备", width = 15)
    @ApiModelProperty(value = "父节点是否是特种设备")
    @TableField(exist = false)
    private Integer pIsSpecialDevice;
    /**专业下是否有子系统标识*/
    @Excel(name = "专业下是否有子系统标识", width = 15)
    @ApiModelProperty(value = "专业下是否有子系统标识")
    @TableField(exist = false)
    private Integer pIsHaveSystem;
    @TableField(exist = false)
    String deviceComposeCode;
    @TableField(exist = false)
    String text;
}
