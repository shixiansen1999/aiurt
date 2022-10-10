package com.aiurt.modules.personnelgroupstatistics.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lkj
 * @Title:
 * @Description:
 * @date 2022/10/09 10:56
 */
@Data
public class TrainTaskDTO {
    @ApiModelProperty("培训任务id")
    private String taskId;
    @ApiModelProperty("培训任务是否需要考试")
    private String examStatus;
}
