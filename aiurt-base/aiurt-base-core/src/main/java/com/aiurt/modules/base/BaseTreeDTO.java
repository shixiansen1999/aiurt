package com.aiurt.modules.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 基础树
 * @author fgw
 */
@Data
public class BaseTreeDTO implements Serializable {

    private static final long serialVersionUID = 565357728427124988L;

    @ApiModelProperty("用于标记结点唯一的id")
    private String id;

    @ApiModelProperty(value = "父节点id")
    private String pid;

    @ApiModelProperty(value = "实际值")
    private String value;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "是否叶子结点  布尔值")
    private Boolean isLeaf;

    private String key;

    private String label;


}
