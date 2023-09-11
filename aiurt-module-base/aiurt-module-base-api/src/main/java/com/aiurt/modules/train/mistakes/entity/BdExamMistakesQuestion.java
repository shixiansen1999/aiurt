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
 * 错题集-考题关联表的实体类
 *
 * @author 华宜威
 * @date 2023-08-24 18:17:48
 */
@Data
@TableName("bd_exam_mistakes_question")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_exam_mistakes_question对象", description = "错题集-考题关联表")
public class BdExamMistakesQuestion extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**错题集id，关联bd_exam_mistakes.id*/
    @ApiModelProperty(value = "错题集id，关联bd_exam_mistakes.id")
    private String mistakesId;

    /**习题id，关联bd_question.id*/
    @ApiModelProperty(value = "习题id，关联bd_question.id")
    private String questionId;

    /**伪删除 0未删除 1已删除*/
    @ApiModelProperty(value = "伪删除 0未删除 1已删除")
    private Integer delFlag;


}
