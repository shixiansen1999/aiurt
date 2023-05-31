package com.aiurt.modules.knowledge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "知识库匹配参数", description = "知识库匹配参数")
public class KnowledgeBaseMatchDTO {

    /**
     * 故障现象集
     */
    @ApiModelProperty("故障现象集")
    private List<String> phenomenons;
    /**
     * 设备类型集
     */
    @ApiModelProperty("设备集")
    private List<String> devices;
    /**
     * 专业
     */
    @ApiModelProperty("专业")
    private String major;
    /**
     * 子系统
     */
    @ApiModelProperty("子系统")
    private String subsystem;
}
