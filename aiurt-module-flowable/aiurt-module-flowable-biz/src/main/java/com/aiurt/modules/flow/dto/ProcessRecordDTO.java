package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author fugaowei
 * @date 2023-10-17
 */


@Data
@ApiModel("流程记录")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRecordDTO implements Serializable {

    @ApiModelProperty(value = "节点id")
    private String nodeId;

    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    @ApiModelProperty(value = "节点办理信息")
    private List<ProcessRecordNodeInfoDTO> nodeList;

    @ApiModelProperty(value = "状态名称")
    private String stateName;

    @ApiModelProperty(value = "状态值")
    private String state;

    private String stateColor;

}
