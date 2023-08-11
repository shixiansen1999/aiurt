package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @className: RepairTaskPrintDTO
 * @author: hqy
 * @date: 2023/8/3 10:13
 * @version: 1.0
 */
@Data
public class RepairTaskPrintDTO {
    /**站点名称*/
    @ApiModelProperty(value = "站点名称")
    private String siteName;
    /**检修人名称*/
    @ApiModelProperty(value = "检修人名称")
    private String overhaulName;
    /**同行人名称*/
    @ApiModelProperty(value = "同行人名称")
    private String peerName;
    /**计划开始时间，精确到分钟*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "计划开始时间，精确到分钟")
    private java.util.Date startTime;
    /**开始时间，精确到分钟*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "开始检修时间")
    private java.util.Date startOverhaulTime;
}
