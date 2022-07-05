package com.aiurt.boot.standard.entity;

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
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_standard")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_standard对象", description="patrol_standard")
public class PatrolStandard implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**标准编号*/
	@Excel(name = "标准编号", width = 15)
    @ApiModelProperty(value = "标准编号")
    private java.lang.String code;
	/**巡检表名*/
	@Excel(name = "巡检表名", width = 15)
    @ApiModelProperty(value = "巡检表名")
    private java.lang.String name;
	/**专业code*/
	@Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private java.lang.String professionCode;
	/**适用系统code*/
	@Excel(name = "适用系统code", width = 15)
    @ApiModelProperty(value = "适用系统code")
    private java.lang.String subsystemCode;
    /**与设备类型相关：0否 1 是*/
    @Excel(name = "与设备类型相关：0否 1 是", width = 15)
    @ApiModelProperty(value = "与设备类型相关：0否 1 是")
    private java.lang.Integer deviceType;
    /**指定具体设备：0否 1 是*/
	@Excel(name = "指定具体设备：0否 1 是", width = 15)
    @ApiModelProperty(value = "指定具体设备：0否 1 是")
    private java.lang.Integer specifyDevice;
	/**设备类型code*/
	@Excel(name = "设备类型code", width = 15)
    @ApiModelProperty(value = "设备类型code")
    private java.lang.String deviceTypeCode;
	/**生效状态：0停用 1启用*/
	@Excel(name = "生效状态：0停用 1启用", width = 15)
    @ApiModelProperty(value = "生效状态：0停用 1启用")
    private java.lang.Integer status;
	/**标准表说明*/
	@Excel(name = "标准表说明", width = 15)
    @ApiModelProperty(value = "标准表说明")
    private java.lang.String remark;
	/**标准制定人ID*/
	@Excel(name = "标准制定人ID", width = 15)
    @ApiModelProperty(value = "标准制定人ID")
    private java.lang.String userId;
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
