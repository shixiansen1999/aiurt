package com.aiurt.modules.train.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.task.DTO
 * @className: BdTrainTaskDTO
 * @author: life-0
 * @date: 2022/4/21 9:35
 * @description: TODO
 * @version: 1.0
 */
@Data
public class BdTrainTaskDTO {
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;
    /**签到状态(1已签到.0未签到)*/
    @Excel(name = "签到状态(1已签到.0未签到)", width = 15)
    @ApiModelProperty(value = "签到状态(1已签到.0未签到)")
    private Integer signState;
    /**反馈转态(1已反馈,0未反馈)*/
    @Excel(name = "反馈转态(1已反馈,0未反馈)", width = 15)
    @ApiModelProperty(value = "反馈转态(1已反馈,0未反馈)")
    private Integer feedState;
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
    /**培训计划开始日期*/
    @Excel(name = "培训计划开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "培训计划开始日期")
    private Date startDate;
    /**培训计划结束日期*/
    @Excel(name = "培训计划结束日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "培训计划结束日期")
    private Date endDate;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "反馈时间")
    private Date completeTime;
    @Excel(name = "培训内容", width = 15)
    @ApiModelProperty(value = "培训内容")
    private String planSubName;
    /**培训部门*/
    @Excel(name = "培训部门", width = 15)
    @ApiModelProperty(value = "培训部门")
    private Integer taskTeamId;
    /**培训部门名称*/
    @Excel(name = "培训部门名称", width = 15)
    @ApiModelProperty(value = "培训部门名称")
    @TableField(exist = false)
    private String taskTeamName;
    /**培训类型*/
    @Excel(name = "培训类型", width = 15)
    @ApiModelProperty(value = "培训类型")
    @Dict(dicCode = "classify_state")
    private Integer classify;
    /**培训地点*/
    @Excel(name = "培训地点", width = 15)
    @ApiModelProperty(value = "培训地点")
    private String address;
    /**培训对象*/
    @Excel(name = "培训对象", width = 15)
    @ApiModelProperty(value = "培训对象")
    private String trainTarget;
    /**任务培训时长*/
    @Excel(name = "任务培训时长", width = 15)
    @ApiModelProperty(value = "任务培训时长")
    private java.math.BigDecimal taskHours;
    /**签到时间*/
    @Excel(name = "签到时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "签到时间")
    private Date signTime;
    /**培训轮数*/
    @Excel(name = "培训轮数", width = 15)
    @ApiModelProperty(value = "培训轮数")
    private Integer number;
    //参培人数
    @ApiModelProperty(value = "参培人数")
    private Integer participantsNumber;
    /**成绩集合*/
    @Excel(name = "成绩集合", width = 15)
    @ApiModelProperty(value = "成绩集合")
    List<TranscriptDTO> transcriptDTOs;
    /**考试试卷（bd_exam_paper表的id）*/
    @Excel(name = "考试试卷（bd_exam_paper表的id）", width = 15)
    @ApiModelProperty(value = "考试试卷（bd_exam_paper表的id）")
    private String examPaperId;
    /**是否作为学习资料(1是，0：否）*/
    @Excel(name = "是否作为学习资料(1是，0：否）", width = 15)
    @ApiModelProperty(value = "是否作为学习资料(1是，0：否）")
    private Integer studyResourceState;
}
