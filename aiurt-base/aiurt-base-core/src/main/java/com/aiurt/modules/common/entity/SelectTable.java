package com.aiurt.modules.common.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zwl
 */
@Data
@ApiModel("下列列表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectTable {

    @ApiModelProperty("id")
    private String id;

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


    private String color;

    /**
     *
     */
    @ApiModelProperty("是否为结构, true为是, false为否")
    private Boolean isOrg = false;

    @ApiModelProperty("部门编码")
    private String orgCode;

    @ApiModelProperty("部门名称")
    private String orgName;

    private Boolean isLeaf;

    public void addChildren(SelectTable child) {
        if (children == null) {
            children = new ArrayList<SelectTable>();
        }
        children.add(child);
    }

    private String title;


    @JsonProperty("pId")
    private String pid;

    private String systemCode;

    private String majorCode;

    /**是否是知识库类别*/
    private Boolean isBaseType;

    @ApiModelProperty(value = "设备类型")
    private String deviceTypeCode;
}
