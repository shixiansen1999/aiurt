package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/7
 * @desc
 */
@Data
public class PatrolTaskAppointSaveDTO  {
    /**任务Id*/
    @Excel(name = "任务Id", width = 15)
    @ApiModelProperty(value = "任务Id")
    private java.lang.String id;
    @Excel(name = "任务编号", width = 15)
    @ApiModelProperty(value = "任务编号")
    @TableField(value = "`planCode`")
    private java.lang.String code;
    /*** 作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3*/
    @Excel(name = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3", width = 15)
    @ApiModelProperty(value = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3")
    private java.lang.Integer type;
    /*** 计划编号*/
    @Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    private java.lang.String planCode;
    /*** 计划令图片*/
    @Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
    private java.lang.String planOrderCodeUrl;
    /*** 巡检开始时间*/
    @ApiModelProperty(value = "巡检开始时间")
    private String startTime;
    /*** 巡检结束时间*/
    @ApiModelProperty(value = "巡检结束时间")
    private String endTime;
    @ApiModelProperty(value = "指派人的信息")
    private List<PatrolAccompanyDTO> accompanyDTOList;
}
