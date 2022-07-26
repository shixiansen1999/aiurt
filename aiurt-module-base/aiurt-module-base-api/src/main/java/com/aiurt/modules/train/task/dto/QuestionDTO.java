package com.aiurt.modules.train.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @projectName: jeecg-boot-parent
 * @package: org.jeecg.modules.train.task.dto
 * @className: QuestionDTO
 * @author: life-0
 * @date: 2022/4/22 10:35
 * @description: TODO
 * @version: 1.0
 */
@Data
public class QuestionDTO {
    /**反馈问题*/
    @Excel(name = "反馈表名", width = 15)
    @ApiModelProperty(value = "反馈表名")
    private String classifyName;
    /**反馈问题*/
    @Excel(name = "反馈问题", width = 15)
    @ApiModelProperty(value = "反馈问题")
    private String questionName;
    /**问题类型(0:单选,1简答)*/
    @Excel(name = "问题类型(0:单选,1简答)", width = 15)
    @ApiModelProperty(value = "问题类型(0:单选,1简答)")
    private Integer questionClassify;
    //单选选项
    private String trainQuestionFeedBackOptionsId;
    /**反馈问题选项*/
    @Excel(name = "反馈问题选项", width = 15)
    @ApiModelProperty(value = "反馈问题选项")
    private String trainQuestionFeedBackOptionsName;
    //简答
    @Excel(name = "简答)", width = 15)
    @ApiModelProperty(value = "简答")
    private String answer;


}
