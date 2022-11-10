package com.aiurt.modules.schedule.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ScheduleCalendarVo {
    public String type;
    public String content;
    public String color;
    private String orgName;

    /**日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
    @TableField(exist = false)
    private Date date;

    /**开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private Date startTime;

    /**结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private  Date  endTime;

    /**时间标记*/
    @Excel(name = "时间标记（0无，1跨日 非字典值）", width = 15)
    @ApiModelProperty(value = "时间标记（0无，1跨日 非字典值）")
    @TableField(exist = false)
    private  String  timeId;
}
