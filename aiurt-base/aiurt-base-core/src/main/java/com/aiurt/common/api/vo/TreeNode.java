package com.aiurt.common.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/6/2511:44
 */
@Data
public class TreeNode {
    @ApiModelProperty("节点id")
    private String id;
    @ApiModelProperty("节点名称")
    private String name;
    @ApiModelProperty("节点父id")
    private String pid;
    @ApiModelProperty("节点孩子")
    List<TreeNode> children = new ArrayList<>();
    @ApiModelProperty("节点id前端使用")
    private String value;
    @ApiModelProperty("节点名称前端使用")
    private String title;
    @ApiModelProperty("是否可以选中 0可选1不可选")
    private boolean disabled;
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    @ApiModelProperty(value = "扩展字段")
    private String extendedField;
    private String color;
}
