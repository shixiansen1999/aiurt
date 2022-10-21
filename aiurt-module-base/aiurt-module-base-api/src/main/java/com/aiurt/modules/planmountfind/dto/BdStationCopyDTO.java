package com.aiurt.modules.planmountfind.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: Sand Sculpture King
 * @date: 2021年05月26日 16:38
 */
@Data
@ApiModel(value = "月计划线路下拉框")
public class BdStationCopyDTO implements Serializable {
    private String id;
    private String name;
    private Integer lineId;
    private String lineName;
}
