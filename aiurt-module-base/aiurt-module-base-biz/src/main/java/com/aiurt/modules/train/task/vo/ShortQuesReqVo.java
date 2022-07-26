package com.aiurt.modules.train.task.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Administrator
 * 2022/4/26
 * 简答题复核内容
 */
@Data
public class ShortQuesReqVo {
    @ApiModelProperty(value = "简答题id")
    private String id;
    @ApiModelProperty(value = "选项")
    private Integer option;
    @ApiModelProperty(value = "分数")
    private String score;
    @ApiModelProperty(value = "考试记录id")
    private String examRecordId;
}
