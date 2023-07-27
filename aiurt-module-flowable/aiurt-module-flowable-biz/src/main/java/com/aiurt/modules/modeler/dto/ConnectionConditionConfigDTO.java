package com.aiurt.modules.modeler.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:wgp
 * @create: 2023-07-27 14:24
 * @Description:
 */
@Data
public class ConnectionConditionConfigDTO implements Serializable {
    @ApiModelProperty("唯一标识")
    private String key;
    @ApiModelProperty("实际值")
    private String value;
    @ApiModelProperty("展示字段")
    private String label;
    @ApiModelProperty("父节点")
    private String pid;
    @ApiModelProperty("孩子节点")
    private List<ConnectionConditionConfigDTO> children;
    @ApiModelProperty("是否可选：false-不可选")
    private Boolean selectable;

    @ApiModelProperty("可选的数据")
    private List<ConnectionConditionConfigDTO> options;

}
