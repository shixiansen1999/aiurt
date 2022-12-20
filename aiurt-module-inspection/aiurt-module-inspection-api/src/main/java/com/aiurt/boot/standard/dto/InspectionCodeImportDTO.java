package com.aiurt.boot.standard.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/19
 * @time: 16:15
 */

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Excel(name = "检修标准表名称", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修标准名称")
    private String title;

    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @Excel(name = "检修周期类型", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    private String type;

    /**专业code,关联cs_major的code*/
    @Excel(name = "专业code", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    private String majorCode;


    /**专业子系统code,关联cs_subsystem_user的code*/
    @Excel(name = "专业子系统code", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    private String subsystemCode;

    /**是否与设备相关(0否1是)*/
    @Excel(name = "与设备类型相关", width = 15,needMerge = true)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    private String isAppointDevice;

    /**状态 0-未生效 1-已生效*/
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    private String status;

    /**设备类型code，关联device_type的code*/
    @Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String deviceTypeCode;



    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  standMistake;

    @ExcelCollection(name = "配置项")
    @ApiModelProperty(value = "配置项")
    @TableField(exist = false)
    private List<InspectionCodeContentDTO> inspectionCodeContentDTOList;
}
