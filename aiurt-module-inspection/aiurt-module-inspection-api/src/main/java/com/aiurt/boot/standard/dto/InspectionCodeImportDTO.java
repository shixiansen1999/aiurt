package com.aiurt.boot.standard.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/19
 * @time: 16:15
 */

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-19 16:15
 */
@Data
public class InspectionCodeImportDTO {

    /**检修标准名称*/
    @Excel(name = "检修标准名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "检修标准名称")
    private String title;

    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    private String type;

    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @Excel(name = "检修周期类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @TableField(exist = false)
    private String cycleType;

    /**专业code,关联cs_major的code*/
    @Excel(name = "适用专业", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    private String majorCode;


    /**专业子系统code,关联cs_subsystem_user的code*/
    @Excel(name = "适用子系统", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    private String subsystemCode;

    /**是否与设备相关(0否1是)*/
    @Excel(name = "与设备类型相关", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    private String isAppointDevice;

    /**适用部门*/
    @Excel(name = "适用部门", width = 15)
    @ApiModelProperty(value = "适用部门")
    @TableField(exist = false)
    private String orgName;

    /**检修表类型*/
    @Excel(name = "检修表类型", width = 15)
    @ApiModelProperty(value = "类型：0应急、车载、2正线、3车辆段 ")
    @TableField(exist = false)
    private String repairTypeName;

    /**状态 0-未生效 1-已生效*/
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    private String status;

    /**设备类型code，关联device_type的code*/
    @Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String deviceTypeCode;



    /**
     * 检修标准错误原因
     */
    @ApiModelProperty(value = "检修标准错误原因")
    @TableField(exist = false)
    private String InspectionCodeErrorReason;

    @ExcelCollection(name = "配置项")
    @ApiModelProperty(value = "配置项")
    @TableField(exist = false)
    private List<InspectionCodeContent> inspectionCodeContentList;
}
