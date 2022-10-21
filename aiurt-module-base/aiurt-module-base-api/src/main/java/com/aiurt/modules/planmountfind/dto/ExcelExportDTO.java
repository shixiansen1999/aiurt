package com.aiurt.modules.planmountfind.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value = "月计划导出dto")
public class ExcelExportDTO {
    /**
     * 序号
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "序号")
    @Excel(name = "序号", width = 15)
    private Integer id;
    /**
     * 作业类别
     */
    @Excel(name = "作业类别", width = 15)
    @ApiModelProperty(value = "作业类别")
    private String type;
    /**
     * 作业单位id
     */
    @Excel(name = "作业单位", width = 15)
    @ApiModelProperty(value = "作业单位id")
    private String departmentId;

    /**
     * taskDate
     */
    @Excel(name = "作业日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "taskDate")
    private Date taskDate;
    /**
     * 作业时间
     */
    @Excel(name = "作业时间", width = 15)
    @ApiModelProperty(value = "作业时间")
    private String taskTime;
    /**
     * 作业范围
     */
    @Excel(name = "线路作业范围", width = 15)
    @ApiModelProperty(value = "线路作业范围")
    private String taskRange;
    /**
     * 供电要求
     */
    @Excel(name = "供电要求", width = 15)
    @ApiModelProperty(value = "供电要求")
    private String powerSupplyRequirement;
    /**
     * 作业内容
     */
    @Excel(name = "作业内容", width = 15)
    @ApiModelProperty(value = "作业内容")
    private String taskContent;
    /**
     * 防护措施
     */
    @Excel(name = "防护措施", width = 15)
    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;

    @Excel(name = "施工负责人", width = 15)
    @ApiModelProperty(value = "施工负责人")
    private String staffName;

    /**
     * 配合部门
     */
    @Excel(name = "配合部门", width = 15)
    @ApiModelProperty(value = "配合部门")
    private String coordinationDepartmentId;

    @Excel(name = "请点车站", width = 15)
    @ApiModelProperty(value = "施工负责人")
    private String qingDianStationName;

    @Excel(name = "销点车站", width = 15)
    @ApiModelProperty(value = "销点车站")
    private String xiaoDianStationName;

    /**
     * 辅站id
     */
    @Excel(name = "辅站", width = 15)
    @ApiModelProperty(value = "辅站id")
    private String assistStationIds;
    /**
     * assistStationManagerIds
     */

    /**
     * 作业人数
     */
    @Excel(name = "作业人数", width = 15)
    @ApiModelProperty(value = "作业人数")
    private Integer taskStaffNum;
    /**
     * 大中型器具
     */
    @Excel(name = "大中型器具", width = 15)
    @ApiModelProperty(value = "大中型器具")
    private String largeAppliances;




}
