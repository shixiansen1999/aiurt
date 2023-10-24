package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * @author fgw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HighLightedNodeDTO implements Serializable {

    private static final long serialVersionUID = 627705881984106406L;

    /**
     * 已完成的任务id
     */
    @ApiModelProperty("已完成的任务id")
    private Set<String> finishedTaskSet;

    /**
     *
     */
    @ApiModelProperty("已完成的节点连线")
    private Set<String> finishedSequenceFlowSet;

    /**
     * 正在进行中的任务节点
     */
    @ApiModelProperty("正在进行中的任务节点")
    private Set<String> unfinishedTaskSet;


    /**
     * model的xml文件
     */
    @ApiModelProperty("model的xml文件")
    private String modelXml;


    /**
     * model的名称
     */
    @ApiModelProperty("model的名称")
    private String modelName;


    @ApiModelProperty("可能会经过的节点")
    private Set<String> featureTaskSet;

    @ApiModelProperty("可能会经过的连线")
    private Set<String> featureSequenceFlowSet;

    @ApiModelProperty("节点办理用户")
    private List<HighLightedUserInfoDTO> highLightedUserInfoDTOs;

    @ApiModelProperty(value = "是否结束，ture是，false 否")
    private Boolean isEnd;


}
