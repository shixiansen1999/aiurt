package com.aiurt.modules.weeklyPlan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class BdStationReturnTypeDTO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "站点名称")
    private String name;

    @ApiModelProperty(value = "所属线路ID")
    private Integer lineId;

    @ApiModelProperty (value = "序号")
    private Integer indexId;

    @ApiModelProperty (value = "线路名称")
    private String lineName;
    /***
     * 该节点的父级
     */
    @ApiModelProperty(value = "该节点的父级")
    private String pid;
    /**
     * 对应bd_line中的id字段,前端数据树中的value
     */
    @ApiModelProperty(value = "前端数据树中的value")
    private String value;
    /**
     * 子集-变电所
     */
    @ApiModelProperty(value = "子集-变电所 ")
    private List<BdStationReturnTypeDTO> children ;
    public void addChildren(BdStationReturnTypeDTO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }
}
