package com.aiurt.boot.standard.entity;

import com.aiurt.modules.basic.entity.DictEntity;
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

/**
 * @Description: patrol_standard_device_type
 * @Author: aiurt
 * @Date:   2023-08-23
 * @Version: V1.0
 */
@Data
@TableName("patrol_standard_device_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_standard_device_type对象", description="patrol_standard_device_type")
public class PatrolStandardDeviceType  extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**巡视标准编码，关联patrol_standard表的code*/
	@Excel(name = "巡视标准编码，关联patrol_standard表的code", width = 15)
    @ApiModelProperty(value = "巡视标准编码，关联patrol_standard表的code")
    private String standardCode;
	/**设备类型code*/
	@Excel(name = "设备类型code", width = 15)
    @ApiModelProperty(value = "设备类型code")
    private String deviceTypeCode;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
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
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    @TableField(exist = false)
    private String deviceTypeName;
}
