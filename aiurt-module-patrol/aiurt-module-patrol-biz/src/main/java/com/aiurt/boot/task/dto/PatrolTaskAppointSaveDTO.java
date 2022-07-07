package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

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
    @TableField(value = "`code`")
    private java.lang.String code;
    /***开始时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;
    /*** 结束时间*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
    @ApiModelProperty(value = "指派人的信息")
    private List<PatrolAccompanyDTO> accompanyDTOList;
}
