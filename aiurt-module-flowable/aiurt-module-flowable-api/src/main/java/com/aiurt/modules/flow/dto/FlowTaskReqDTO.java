package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wgp
 * @Title:
 * @Description: 待办任务查询条件
 * @date 2022/7/2617:32
 */
@Data
public class FlowTaskReqDTO {
    @ApiModelProperty("流程标识")
    private String processDefinitionKey;
    @ApiModelProperty("流程定义名")
    private String processDefinitionName;
    @ApiModelProperty("任务名称")
    private String taskName;


    private Integer pageNo;
    private Integer pageSize;
}
