package com.aiurt.config.datafilter.entity;

import lombok.Data;

import java.util.Set;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/616:49
 */
@Data
public class ModelDataPermInfo {
    private Set<String> excludeMethodNameSet;
    private String userFilterColumn;
    private String deptFilterColumn;
    private String lineFilterColumn;
    private String stationFilterColumn;
    private String majorFilterColumn;
    private String systemFilterColumn;
    private String mainTableName;
    private Boolean mustIncludeUserRule;
}
