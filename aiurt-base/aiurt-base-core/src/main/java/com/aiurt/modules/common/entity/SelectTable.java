package com.aiurt.modules.common.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("下列列表")
public class SelectTable {

    private String key;

    private String value;

    private String label;

    private List<SelectTable> children;

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
}
