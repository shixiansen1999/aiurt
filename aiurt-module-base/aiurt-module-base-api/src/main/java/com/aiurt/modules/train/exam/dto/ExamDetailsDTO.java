package com.aiurt.modules.train.exam.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.train.task.dto.TranscriptDTO;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.eaxm.dto
 * @className: ExamDetailsDTO
 * @author: life-0
 * @date: 2022/4/26 10:19
 * @description: TODO
 * @version: 1.0
 */
@Data
public class ExamDetailsDTO {


    /**计划任务状态*/
    @ApiModelProperty(value = "是否已经发布考试结果,0未开始、1进行中，2待复核，3已结束")
    @TableField(exist = false)
    private Integer isRelease;

    /**试卷名称*/
    @ApiModelProperty(value = "考试计划名称")
    @TableField(exist = false)
    private String paperName;

    /**题目数量*/
    @ApiModelProperty(value = "题目数量")
    @TableField(exist = false)
    private Integer number;

    /**及格分*/
    @ApiModelProperty(value = "及格分")
    @TableField(exist = false)
    private Integer passcode;

    /**总分*/
    @ApiModelProperty(value = "总分")
    @TableField(exist = false)
    private Integer paperScore;

    /**考试类型*/
    @Excel(name = "考试类型", width = 15)
    @ApiModelProperty(value = "考试类型")
    @Dict(dicCode = "examClassify_state")
    private Integer examClassify;
    /**考试日期*/
    @Excel(name = "考试日期", width = 15, format = "yyyy-MM-dd  HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd  HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd  HH:mm")
    @ApiModelProperty(value = "考试日期")
    private Date examPlanTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "考试时间")
    @TableField(exist = false)
    private Date examTime;
    /**考试截止时间*/
    @ApiModelProperty(value = "考试截止时间")
    @TableField(exist = false)
    private String  examinationDeadline;

    /**培训部门名称*/
    @ApiModelProperty(value = "培训部门名称")
    @TableField(exist = false)
    private String taskTeamName;

    @ApiModelProperty(value = "缺考人数")
    private Integer absenteesNumber;
    @ApiModelProperty(value = "应参考人数")
    private Integer referenceNumber;
    @ApiModelProperty(value = "实际参加考试人数")
    private Integer actualReferenceNumber;
    @ApiModelProperty(value = "及格人数")
    private Integer passesNumber;
    @ApiModelProperty(value = "缺考人员姓名")
    List<String> absentPersonNames;

    @ApiModelProperty(value = "考试成绩")
    List<TranscriptDTO> transcriptDTOS;


}
