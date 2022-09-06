package com.aiurt.boot.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 首页巡检的组织机构DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexOrgDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 组织机构编号
     */
    @ApiModelProperty(value = "组织机构编号")
    private java.lang.String orgCode;
    /**
     * 组织机构名称
     */
    @ApiModelProperty(value = "组织机构名称")
    private java.lang.String orgName;
}
