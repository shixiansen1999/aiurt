package com.aiurt.modules.train.task.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Administrator
 * 2022/4/26
 * 简答题详情
 */
@Data
public class ShortAnswerVo {
    @ApiModelProperty(value = "简答题id")
    private String id;
    @ApiModelProperty(value = "考生答案")
    private String stuAnswer;
    @ApiModelProperty(value = "简答题题目")
    private String question;
    @ApiModelProperty(value = "标准答案")
    private String answer;

}
