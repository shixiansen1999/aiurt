package com.aiurt.boot.standard.entity;

import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.boot.manager.dto.OrgVO;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
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
public class InspectionCode extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    /**检修标准名称*/
    @Excel(name = "检修标准表名称", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修标准名称")
    @JsonProperty("name")
    private java.lang.String title;
	/**检修标准编码*/
	@Excel(name = "检修标准表编码", width = 15)
    @ApiModelProperty(value = "检修标准编码")
    private java.lang.String code;
	/**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @Dict(dicCode = "inspection_cycle_type")
    private java.lang.Integer type;

    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @Excel(name = "检修周期类型", width = 15,dicCode = "inspection_cycle_type")
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @Dict(dicCode = "inspection_cycle_type")
    @TableField(exist = false)
    private java.lang.String cycleType;

    /**专业code,关联cs_major的code*/
    @Excel(name = "适用专业", width = 15,dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @MajorFilterColumn
    private java.lang.String majorCode;

    /**专业子系统code,关联cs_subsystem_user的code*/
    @Excel(name = "适用子系统", width = 15,dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private java.lang.String subsystemCode;

	/**是否与设备相关(0否1是)*/
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    @Dict(dicCode = "is_appoint_device")
    private java.lang.Integer isAppointDevice;
    /**
     * 是否合并工单：0否、1是
     */
    @Excel(name = "是否合并工单：0否、1是", width = 15)
    @ApiModelProperty(value = "是否合并工单：0否、1是")
    @Dict(dicCode = "is_merge_device")
    private java.lang.Integer isMergeDevice;

    /**是否与设备相关(0否1是)*/
    @Excel(name = "与设备类型相关", width = 15,dicCode = "is_appoint_device")
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    @TableField(exist = false)
    private java.lang.String isRelatedDevice;

    /**设备类型code，关联device_type的code*/
    @Excel(name = "设备类型", width = 15,dictTable = "device_type", dicText = "name", dicCode = "code")
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Dict(dictTable = "device_type", dicText = "name", dicCode = "code")
    private java.lang.String deviceTypeCode;

    /**状态 0-未生效 1-已生效*/
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    @Dict(dicCode = "is_take_effect")
    private java.lang.Integer status;

    /**状态 0-未生效 1-已生效*/
    @Excel(name = "生效状态", width = 15,dicCode = "is_take_effect")
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
//    @Dict(dicCode = "is_take_effect")
    @TableField(exist = false)
    private java.lang.String effectStatus;


	/**删除状态 0.未删除 1已删除*/
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
    /**
     * 是否指定设备
     */
    @ApiModelProperty(value = "是否指定设备")
    @TableField(exist = false)
    private java.lang.String specifyDevice;
    @ApiModelProperty(value = "打印模板文件名称(来源字典值)")
    private java.lang.String printTemplate;
    @ExcelCollection(name = "配置项")
    @ApiModelProperty(value = "配置项")
    @TableField(exist = false)
    private List<InspectionCodeContent> inspectionCodeContentList;

    /**
     * 组织机构
     */
    @ApiModelProperty(value = "组织机构")
    @TableField(exist = false)
    private List<OrgVO> orgCodeList;
    /**
     * 组织机构名称
     */
    @ApiModelProperty(value = "组织机构名称")
    @TableField(exist = false)
    private java.lang.String orgName;

    @Excel(name = "备件清单", width = 15)
    @ApiModelProperty(value = "备件清单")
    private String sparePartsList;
    @Excel(name = "材料清单", width = 15)
    @ApiModelProperty(value = "材料清单")
    private String materialList;
    @Excel(name = "工器具清单", width = 15)
    @ApiModelProperty(value = "工器具清单")
    private String workToolsList;

    @Excel(name = "类型：0应急、车载、2正线、3车辆段 ", width = 15)
    @ApiModelProperty(value = "类型：0应急、车载、2正线、3车辆段 ")
    private Integer repairType;

    @Excel(name = "类型：0应急、车载、2正线、3车辆段 ", width = 15)
    @TableField(exist = false)
    private String repairTypeValue;

    /**
     * 设备类型多选
     */
    @ApiModelProperty(value = "设备类型多选")
    @TableField(exist = false)
    private List<String> deviceTypeCodeList;
}
