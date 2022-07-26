package com.aiurt.modules.train.task.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import com.aiurt.modules.train.task.entity.BdTrainTeacherFeedbackRecord;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/4/22
 * @desc
 */
@Data
@ApiModel(value = "讲师授课记录dto")
public class BdTeachTranTaskDTO {
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private String userId;

    @Excel(name = "任务计划名称", width = 15)
    @ApiModelProperty(value = "任务计划名称")
    private String taskName;

    /**签到状态(1已签到.0未签到)*/
    @Excel(name = "签到状态(1已签到.0未签到)", width = 15)
    @ApiModelProperty(value = "签到状态(1已签到.0未签到)")
    private Integer signState;

    /**培训部门*/
    @Excel(name = "培训部门", width = 15)
    @ApiModelProperty(value = "培训部门")
    private Integer taskTeamId;

    /**讲师名称*/
    @Excel(name = "讲师name", width = 15)
    @ApiModelProperty(value = "讲师name")
    @TableField(exist = false)
    private String teacherName;


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


    /**实际开始培训时间*/
    @Excel(name = "实际开始培训时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始培训时间")
    private Date startTime;

    /**结束时间*/
    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**培训类型*/
    @Excel(name = "培训类型", width = 15)
    @ApiModelProperty(value = "培训类型")
    @Dict(dictTable = "bd_train_task", dicText = "classify_name", dicCode = "id")
    private Integer classify;

    /**培训内容*/
    @Excel(name = "培训内容", width = 15)
    @ApiModelProperty(value = "培训内容")
    private String planSubName;

    /**培训课时*/
    @Excel(name = "培训课时", width = 15)
    @ApiModelProperty(value = "培训课时")
    private Integer courseHours;

    /**培训对象*/
    @Excel(name = "培训对象", width = 15)
    @ApiModelProperty(value = "培训对象")
    private String trainTarget;

    /**培训地点*/
    @Excel(name = "培训地点", width = 15)
    @ApiModelProperty(value = "培训地点")
    private String address;

    /**开始培训日期范围**/
    @Excel(name = "开始培训日期范围",width = 15)
    @ApiModelProperty(value = "开始培训日期范围")
    @TableField(exist = false)
    private String startTrainingDateRange;

    /**参训人次*/
    @Excel(name = "参训人次", width = 15)
    @ApiModelProperty(value = "参训人次")
    @TableField(exist = false)
    private Integer participantsNumber;

    /**未参训人次(缺席人数)*/
    @Excel(name = "参训人数", width = 15)
    @ApiModelProperty(value = "参训人数")
    @TableField(exist = false)
    private Integer notParticipantsNumber;

    /**是否进行培训考试(1是，0：否）*/
    @Excel(name = "是否进行培训考试(1是，0：否）", width = 15)
    @ApiModelProperty(value = "是否进行培训考试(1是，0：否）")
    @Dict( dicCode = "examStatus_type")
    private Integer examStatus;

    /**上传课件集合*/
    @Excel(name = "上传课件集合", width = 15)
    @ApiModelProperty(value = "上传课件集合")
    @TableField(exist = false)
    private List<BdQuestionOptionsAtt> uploadCourseWareList;

    /**及格人数*/
    @Excel(name = "及格人数", width = 15)
    @ApiModelProperty(value = "及格人数")
    @TableField(exist = false)
    private Integer passedNumber;

    /**未及格人数*/
    @Excel(name = "未及格人数", width = 15)
    @ApiModelProperty(value = "未及格人数")
    @TableField(exist = false)
    private Integer notPassedNumber;

    /**缺考人数*/
    @Excel(name = "缺考人数", width = 15)
    @ApiModelProperty(value = "缺考人数")
    @TableField(exist = false)
    private Integer missingExaminationNumber;

    /**授课老师-授课记录（已关闭）-查看评估*/
    @Excel(name = "查看评估集合", width = 15)
    @ApiModelProperty(value = "查看评估集合集合")
    @TableField(exist = false)
    private List<BdTrainTeacherFeedbackRecord> assessmentList;


}
