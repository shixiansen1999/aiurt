package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author fgw
 */
@Data
public class StartBpmnImportDTO implements Serializable {

    @ApiModelProperty("流程定义key")
    private String modelKey;

    @ApiModelProperty("业务数据")
    private Map<String, Object> busData;

    @ApiModelProperty("导入用户")
    private String userName;

    @ApiModelProperty("主键id")
    private String businessKey;

    @ApiModelProperty("流程审批批注对象")
    private FlowTaskCompleteCommentDTO flowTaskCompleteDTO;
}
