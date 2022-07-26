package com.aiurt.modules.train.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.task.dto
 * @className: FeedBackDetailsDTO
 * @author: life-0
 * @date: 2022/4/22 10:11
 * @description: TODO
 * @version: 1.0
 */
@Data
public class FeedBackDetailsDTO {
    /**任务计划名称*/
    @Excel(name = "任务计划名称", width = 15)
    @ApiModelProperty(value = "任务计划名称")
    private String taskName;
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
    /**培训部门*/
    @Excel(name = "培训部门", width = 15)
    @ApiModelProperty(value = "培训部门")
    private Integer taskTeamId;
    /**培训部门名称*/
    @Excel(name = "培训部门名称", width = 15)
    @ApiModelProperty(value = "培训部门名称")
    @TableField(exist = false)
    private String taskTeamName;
    /**讲师名字*/
    @Excel(name = "讲师名字", width = 15)
    @ApiModelProperty(value = "讲师名字")
    private String teacherName;
    /**讲师id*/
    @Excel(name = "讲师Id", width = 15)
    @ApiModelProperty(value = "讲师ID")
    private String teacherId;
    /**反馈类别*/
    @Excel(name = "学生反馈表名", width = 15)
    @ApiModelProperty(value = "学生反馈表名")
    List<String> classifyName;
    /**反馈类别*/
    @Excel(name = "教师反馈表名", width = 15)
    @ApiModelProperty(value = "教师反馈表名")
    List<String> teacherClassifyName;
    //学生反馈评价问题
    @Excel(name = "学生反馈", width = 15)
    @ApiModelProperty(value = "学生反馈")
    List<QuestionDTO>questionDTOs;
    //评价问题
    @Excel(name = "学生反馈表", width = 15)
    @ApiModelProperty(value = "学生反馈表")
    List<List<QuestionDTO>>stuQuestionDTOs;



}
