package com.aiurt.boot.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 首页巡检的巡检用户DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private java.lang.String userId;
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private java.lang.String userName;
}
