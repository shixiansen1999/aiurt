package com.aiurt.modules.train.exam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zwl
 */
@Data
public class BdExamRecordDTO {
    @ApiModelProperty("习题id")
    private  String  exercisesId;

    @ApiModelProperty("选项Id")
    private String [] optionId;

    @ApiModelProperty("题目类别")
    private Integer  topicCategory;

    @ApiModelProperty("考生答案")
    private String content;

}
