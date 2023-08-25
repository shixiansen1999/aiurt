package com.aiurt.modules.train.mistakes.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 错题集答题情况表的实体类
 *
 * @author 华宜威
 * @date 2023-08-24 18:21:35
 */
@Data
@TableName("bd_exam_mistakes_answer")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_exam_mistakes_answer对象", description = "错题集答题情况表")
public class BdExamMistakesAnswer extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**错题集id，关联bd_exam_mistakes.id*/
    @ApiModelProperty(value = "错题集id，关联bd_exam_mistakes.id")
    private String mistakesId;

    /**习题id，关联bd_question.id*/
    @ApiModelProperty(value = "习题id，关联bd_question.id")
    private String questionId;

    /**1正确2错误3不完全正确*/
    @ApiModelProperty(value = "1正确2错误3不完全正确")
    private Integer isTrue;

    /**本题得分*/
    @ApiModelProperty(value = "本题得分")
    private Integer score;

    /**考生答案*/
    @ApiModelProperty(value = "考生答案")
    private String answer;

    /**伪删除 0未删除 1已删除*/
    @ApiModelProperty(value = "伪删除 0未删除 1已删除")
    private Integer delFlag;

}
