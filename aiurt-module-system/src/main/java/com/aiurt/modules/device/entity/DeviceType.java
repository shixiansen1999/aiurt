package com.aiurt.modules.device.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
	@Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    private String majorCode;
	/**系统编号*/
	@Excel(name = "系统编号", width = 15)
    @ApiModelProperty(value = "系统编号")
    private String systemCode;
	/**分类编号*/
	@Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
    private String code;
	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
    private String name;
	/**状态 0-停用 1-正常*/
	@Excel(name = "状态 0-停用 1-正常", width = 15)
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    @Dict(dicCode = "device_type_status")
    private Integer status;
	/**是否为特种设备(1:是,0:否)*/
	@Excel(name = "是否为特种设备(1:是,0:否)", width = 15)
    @ApiModelProperty(value = "是否为特种设备(1:是,0:否)")
    @Dict(dicCode = "is_special_device")
    private Integer isSpecialDevice;
	/**是否尾节点(1:是,0：否)*/
	@Excel(name = "是否尾节点(1:是,0：否)", width = 15)
    @ApiModelProperty(value = "是否尾节点(1:是,0：否)")
    @Dict(dicCode = "is_end")
    private Integer isEnd;
	/**层级结构*/
	@Excel(name = "层级结构", width = 15)
    @ApiModelProperty(value = "层级结构")
    private String codeCc;
	/**上级节点*/
	@Excel(name = "上级节点", width = 15)
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
    @Excel(name = "设备类型子集", width = 15)
    @ApiModelProperty(value = "设备类型子集")
    @TableField(exist = false)
    private List<DeviceType> children;
    /**设备组成*/
    @Excel(name = "设备组成", width = 15)
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
}
