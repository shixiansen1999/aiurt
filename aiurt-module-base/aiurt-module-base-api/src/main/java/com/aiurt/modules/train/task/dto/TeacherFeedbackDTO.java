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
 * @package: org.jeecg.modules.train.task.dto
 * @className: TeacherFeedbackDTO
 * @author: life-0
 * @date: 2022/4/25 14:10
 * @description: TODO
 * @version: 1.0
 */
@Data
public class TeacherFeedbackDTO {
    /**任务计划名称*/
    @ApiModelProperty(value = "任务计划名称")
    private String taskName;
    /**培训部门名称*/
    @ApiModelProperty(value = "培训部门名称")
    @TableField(exist = false)
    private String taskTeamName;
    /**讲师名字*/
    @ApiModelProperty(value = "讲师名字")
    private String teacherName;
    /**培训计划开始日期*/
    @Excel(name = "培训计划开始日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "培训计划开始日期")
    private Date startDate;

    @ApiModelProperty(value = "参加人数")
    private Integer participantsNumber;
    @ApiModelProperty(value = "实际人数")
    private Integer actualNumber;
    @ApiModelProperty(value = "缺少人数")
    private Integer lackNumber;
    /**是否进行培训考试(1是，0：否）*/
    @ApiModelProperty(value = "是否进行培训考试(1是，0：否）")
    @Dict( dicCode = "examStatus_type")
    private Integer examStatus;
    @ApiModelProperty(value = "及格人数")
    private Integer qualifiedPersonNumber;
    @ApiModelProperty(value = "不及格人数")
    private Integer failedPersonNumber;
    @ApiModelProperty(value = "缺考人数")
    private Integer absenteesNumber;
    @ApiModelProperty(value = "应参考人数")
    private Integer referenceNumber;
    @ApiModelProperty(value = "实际参加考试人数")
    private Integer actualReferenceNumber;
    //反馈
    List<QuestionDTO> questionDTOs;

}
