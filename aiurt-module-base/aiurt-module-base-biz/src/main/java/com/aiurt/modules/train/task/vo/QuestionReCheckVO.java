package com.aiurt.modules.train.task.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;

import java.util.List;

/**
 * Administrator
 * 2022/4/26
 * 复核考卷
 */
@Data
public class QuestionReCheckVO {
    @ApiModelProperty(value = "多媒体")
    private List<BdQuestionOptionsAtt> mideas;
    @ApiModelProperty(value = "简答题")
    private ShortAnswerVo shortAnswerVos;
}
