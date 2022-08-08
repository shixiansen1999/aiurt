package com.aiurt.modules.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
    private Set<String> finishedTaskSet;

    /**
     *
     */
    private Set<String> finishedSequenceFlowSet;

    /**
     * 正在进行中的任务节点
     */
    private Set<String> unfinishedTaskSet;


    /**
     * model的xml文件
     */
    private String modelXml;
    /**
     * model的名称
     */
    private String modelName;


}
