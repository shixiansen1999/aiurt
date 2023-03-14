package com.aiurt.boot.standard.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

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
public class InspectionCodeExcelDTO extends DictEntity  {

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private String id;
    /**检修标准名称*/
    @Excel(name = "检修标准表名称", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修标准名称")
    private String title;
	/**检修标准编码*/
    @ApiModelProperty(value = "检修标准编码")
    private String code;
	/**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @Dict(dicCode = "inspection_cycle_type")
    private Integer type;

    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @Excel(name = "检修周期类型", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @Dict(dicCode = "inspection_cycle_type")
    @TableField(exist = false)
    private String cycleType;

    /**专业code,关联cs_major的code*/
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @MajorFilterColumn
    private String majorCode;

    @Excel(name = "适用专业", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    @TableField(exist = false)
    private String majorName;

    /**专业子系统code,关联cs_subsystem_user的code*/
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private String subsystemCode;

    @Excel(name = "适用子系统", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    @TableField(exist = false)
    private String subsystemName;

	/**是否与设备相关(0否1是)*/
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    @Dict(dicCode = "is_appoint_device")
    private Integer isAppointDevice;

    /**是否与设备相关(0否1是)*/
    @Excel(name = "与设备类型相关", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    @TableField(exist = false)
    private String isRelatedDevice;

    /**设备类型code，关联device_type的code*/
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Dict(dictTable = "device_type", dicText = "name", dicCode = "code")
    private String deviceTypeCode;

    @Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @TableField(exist = false)
    private String deviceTypeName;

    /**状态 0-未生效 1-已生效*/
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    @Dict(dicCode = "is_take_effect")
    private Integer status;

    /**状态 0-未生效 1-已生效*/
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    @TableField(exist = false)
    private String effectStatus;


	/**删除状态 0.未删除 1已删除*/
    @ApiModelProperty(value = "删除状态 0.未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
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
    /**
     * 是否指定设备
     */
    @ApiModelProperty(value = "是否指定设备")
    @TableField(exist = false)
    private String specifyDevice;

    @ExcelCollection(name = "配置项")
    @ApiModelProperty(value = "配置项")
    @TableField(exist = false)
    private List<InspectionCodeContentDTO> inspectionCodeContentDTOList;

    @Excel(name = "类型：0应急、车载、2正线、3车辆段 ", width = 15)
    @ApiModelProperty(value = "类型：0应急、车载、2正线、3车辆段 ")
    private Integer repairType;

}
