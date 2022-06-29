package com.aiurt.boot.standard.entity;

import com.aiurt.common.aspect.annotation.Dict;
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
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("inspection_code")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="inspection_code对象", description="inspection_code")
public class InspectionCode implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**检修标准编码*/
	@Excel(name = "检修标准编码", width = 15)
    @ApiModelProperty(value = "检修标准编码")
    private java.lang.String code;
	/**检修标准名称*/
	@Excel(name = "检修标准名称", width = 15)
    @ApiModelProperty(value = "检修标准名称")
    private java.lang.String title;
	/**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
	@Excel(name = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)", width = 15)
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @Dict(dicCode = "inspection_cycle_type")
    private java.lang.Integer type;
	/**状态 0-未生效 1-已生效*/
	@Excel(name = "状态 0-未生效 1-已生效", width = 15)
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    private java.lang.Integer status;
	/**设备类型code，关联device_type的code*/
	@Excel(name = "设备类型code，关联device_type的code", width = 15)
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @Dict(dictTable = "device_type", dicText = "name", dicCode = "code")
    private java.lang.String deviceTypeCode;
	/**是否与设备相关(0否1是)*/
	@Excel(name = "是否与设备相关(0否1是)", width = 15)
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    @Dict(dicCode = "is_appoint_device")
    private java.lang.Integer isAppointDevice;
	/**专业code,关联cs_major的code*/
	@Excel(name = "专业code,关联cs_major的code", width = 15)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private java.lang.String majorCode;
	/**专业子系统code,关联cs_subsystem_user的code*/
	@Excel(name = "专业子系统code,关联cs_subsystem_user的code", width = 15)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private java.lang.String subsystemCode;
	/**删除状态 0.未删除 1已删除*/
	@Excel(name = "删除状态 0.未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0.未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
