package com.aiurt.modules.train.mistakes.dto.other;

import com.aiurt.modules.train.question.dto.BdQuestionOptionsDTO;
import com.aiurt.modules.train.question.entity.BdQuestionOptionsAtt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 考题的DTO对象，目前用于展示，看后期能不能把所有的类型的题目都统一起来
 *
 * @author 华宜威
 * @date 2023-08-25 15:18:31
 */
@Data
public class QuestionDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**题目内容*/
    @ApiModelProperty(value = "题目内容")
    private String content;

    /**题目类型（1选择题、2多选题、3简答题）*/
    @ApiModelProperty(value = "题目类型（1选择题、2多选题、3简答题）")
    private Integer queType;

    // 下面的先注释，看后期如果统一题目类型有没有用
    // /**简答题答案*/
    // @ApiModelProperty(value = "简答题答案")
    // private String answer;

    // /**选择题(单选或者多选)答案*/
    // @ApiModelProperty(value = "选择题(单选或者多选)答案")
    // private List<String> optionContentList;

    /**题目所拥有的选项*/
    @ApiModelProperty(value = "题目所拥有的选项")
    private List<BdQuestionOptionsDTO> bdQuestionOptionsDTOList;

    /**标准答案，如果是选择题，多个使用中文逗号隔开*/
    @ApiModelProperty(value = "标准答案，如果是选择题，多个使用中文逗号隔开")
    private String answer;

    /**考生答案，如果是选择题，多个使用中文逗号隔开*/
    @ApiModelProperty(value = "考生答案")
    private String stuAnswer;

    @ApiModelProperty(value = "多媒体")
    private List<BdQuestionOptionsAtt> mideas;

}
