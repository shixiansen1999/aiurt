package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 待办任务查询条件
 * @date 2022/7/2617:32
 */
@Data
public class FlowCopyReqDTO {
    @ApiModelProperty("流程实例Id")
    private String processInstanceId;
    @ApiModelProperty("流程实例名称")
    private String processInstanceName;

    private Integer pageNo;
    private Integer pageSize;
}
