package com.aiurt.modules.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("下列列表")
public class SelectTable {

    private String key;

    private String value;

    private String label;

    private List<SelectTable> children ;

    private Integer level;

    /**
     * 针对位置管理: 线路
     */
    private String lineCode;

    /**
     * 针对位置管理: 站所
     */
    private String stationCode;

    /**
     * 位置
     */
    private String positionCode;

    /**
     *
     */
    @ApiModelProperty(value = "人员数量")
    private Long userNum;

    /**
     *
     */
    private String parentValue;

    /**
     *
     */
    @ApiModelProperty("是否为结构, true为是, false为否")
    private Boolean isOrg = false;

    @ApiModelProperty("部门编码")
    private String orgCode;

    @ApiModelProperty("部门名称")
    private String orgName;

    public void addChildren(SelectTable child) {
        if (children == null) {
            children = new ArrayList<SelectTable>();
        }
        children.add(child);
    }
}
