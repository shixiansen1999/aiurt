package com.aiurt.modules.train.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.task.dto
 * @className: TrainQueryTableDTO
 * @author: life-0
 * @date: 2022/4/21 9:45
 * @description: TODO
 * @version: 1.0
 */
@Data
@ApiModel(value = "培训记录dto")
public class TrainQueryTableDTO {

    /**主键*/
    @ApiModelProperty(value = "uesr表id")
    private String id;
    @ApiModelProperty(value = "任务表id")
    private String taskId;
    /**签到状态(1已签到.0未签到)*/
    @Excel(name = "签到状态(1已签到.0未签到)", width = 15)
    @ApiModelProperty(value = "签到状态(1已签到.0未签到)")
    private Integer signState;
    /**反馈转态(1已反馈,0未反馈)*/
    @Excel(name = "反馈转态(1已反馈,0未反馈)", width = 15)
    @ApiModelProperty(value = "反馈转态(1已反馈,0未反馈)")
    private Integer feedState;
    /**讲师名字*/
    @Excel(name = "讲师名字", width = 15)
    @ApiModelProperty(value = "讲师名字")
    private String teacherName;
    /**实际开始培训时间*/
    @Excel(name = "实际开始培训时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "实际开始培训时间")
    private Date startTime;
    /**结束时间*/
    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    /**任务计划名称*/
    @Excel(name = "任务计划名称", width = 15)
    @ApiModelProperty(value = "任务计划名称")
    private String taskName;
}
