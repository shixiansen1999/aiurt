package com.aiurt.modules.train.mistakes.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 错题集接口的列表请求DTO
 *
 * @author 华宜威
 * @date 2023-08-25 11:39:41
 */
@Data
public class BdExamMistakesReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**培训任务id，关联bd_train_task.id*/
    @ApiModelProperty(value = "考培训任务id，关联bd_train_task.id")
    private String trainTaskId;

    /**页数*/
    @ApiModelProperty(value = "页数")
    private Integer pageNo;

    /**每页条数*/
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

}
