package com.aiurt.boot.rehearsal.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author
 * @date 2022/11/30 20:10
 * @description: 参与部门DTO
 */
@Data
@AllArgsConstructor
public class EmergencyDeptDTO {
    @ApiModelProperty(value = "部门编码")
    private String orgCode;
    @ApiModelProperty(value = "部门名称")
    private String orgName;
}
