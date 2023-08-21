package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-08-11 15:44
 * @Description:
 */
@Data
public class ProcessParticipantsReqDTO {
    @ApiModelProperty("流程实例id")
    private String processInstanceId;
    @ApiModelProperty("任务id")
    private String taskId;
    @ApiModelProperty("表单数据")
    private Map<String,Object> busData;

    @ApiModelProperty("流程标识")
    private String modelKey;

    @ApiModelProperty("按钮类型")
    private String approvalType;

}
