package com.aiurt.modules.train.question.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author lkj
 */
@Data
public class BdQuestionDTO {


    @ApiModelProperty("选择题数量")
    private Integer choiceQuestionNum;

    @ApiModelProperty("简答题数量")
    private  Integer shortAnswerQuestionNum;
}
