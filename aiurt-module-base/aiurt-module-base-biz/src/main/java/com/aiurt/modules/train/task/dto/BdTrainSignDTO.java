package com.aiurt.modules.train.task.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.modules.train.task.entity.BdTrainTaskSign;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author lkj
 */
@Data
public class BdTrainSignDTO {

    /**培训部门名称*/
    @Excel(name = "培训部门名称", width = 15)
    @ApiModelProperty(value = "培训部门名称")
    private String taskTeamName;

    /**任务计划名称*/
    @Excel(name = "任务计划名称", width = 15)
    @ApiModelProperty(value = "任务计划名称")
    private String taskName;

    /**当前轮数*/
    @Excel(name = "当前轮数", width = 15)
    @ApiModelProperty(value = "当前轮数")
    private Integer num;

    /**总培训人数*/
    @Excel(name = "总培训人数", width = 15)
    @ApiModelProperty(value = "总培训人数")
    private Integer numbers;

    /**总培训人数*/
    @Excel(name = "当前轮数培训人数", width = 15)
    @ApiModelProperty(value = "当前轮数培训人数")
    private Integer userNum;

    /**当前轮数签到记录*/
    @Excel(name = "当前轮数签到记录", width = 15)
    @ApiModelProperty(value = "当前轮数签到记录")
    private List<BdTrainTaskSign> bdTrainTaskSignList;

    /**实际开始培训时间*/
    @Excel(name = "实际开始培训时间", width = 15, format = "yyyy-MM-dd  HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm")
    @ApiModelProperty(value = "实际开始培训时间")
    private Date startTime;
    /**实际关闭培训时间*/
    @Excel(name = "实际关闭培训时间", width = 15, format = "yyyy-MM-dd  HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "实际关闭培训时间")
    private Date endTime;



}
