package com.aiurt.modules.train.exam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 导入
 * @author zwl
 */
@Data
public class BdExamIdDTO {

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("任务id")
    private  String trainTaskId;

    @ApiModelProperty("考试类别")
    private Integer examClassify;

    @ApiModelProperty("试卷id")
    private  String examPaperId;
}
