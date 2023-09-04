package com.aiurt.modules.train.question.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 习题选项的DTO对象
 *
 * @author 华宜威
 * @date 2023-08-25 16:27:36
 */
@Data
public class BdQuestionOptionsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @ApiModelProperty(value = "主键")
    private String id;
    /**习题id,关联习题表的主键*/
    @ApiModelProperty(value = "习题id,关联习题表的主键")
    private String questionId;
    /**选项内容*/
    @ApiModelProperty(value = "选项内容")
    private String content;
    /**是否正确 正确1 错误0*/
    @ApiModelProperty(value = "是否正确 正确1 错误0")
    private Integer isRight;

}
