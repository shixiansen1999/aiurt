package com.aiurt.modules.train.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.modules.train.feedback.entity.BdTrainQuestionFeedbackOptions;
import com.aiurt.modules.train.task.entity.BdTrainStudentFeedbackRecord;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@ApiModel(value = "学员反馈记录dto")
public class StudentFeedbackRecordDTO {

    /**反馈意见表名*/
    @Excel(name = "反馈意见表名", width = 15)
    @ApiModelProperty(value = "反馈意见表名")
    private String name;

    /**反馈类别*/
    @Excel(name = "反馈类别", width = 15)
    @ApiModelProperty(value = "反馈类别")
    private String classifyName;

    /**问题类型(0:单选,1简答)*/
    @Excel(name = "问题类型(0:单选,1简答)", width = 15)
    @ApiModelProperty(value = "问题类型(0:单选,1简答)")
    private Integer questionClassify;

    /**问题反馈选项*/
    @Excel(name = "问题反馈选项", width = 15)
    @ApiModelProperty(value = "问题反馈单选项")
    private List<BdTrainQuestionFeedbackOptions> options;

    /**学员问题反馈表*/
    @Excel(name = "学员问题反馈表", width = 15)
    @ApiModelProperty(value = "学员问题反馈表")
    private List<BdTrainStudentFeedbackRecord> bdTrainStudentFeedbackRecords;

}
