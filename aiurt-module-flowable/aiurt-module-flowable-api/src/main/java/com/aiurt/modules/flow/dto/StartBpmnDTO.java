package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * @author fgw
 */
@ApiModel(value = "启动流程")
@Data
public class StartBpmnDTO implements Serializable {

    @ApiModelProperty("流程定义key")
    @NotBlank(message = "参数错误， 流程定义key不能为空")
    private String modelKey;

    /**
     * 业务数据
     */
    @ApiModelProperty("业务数据")
    private Map<String, Object> busData;
}
