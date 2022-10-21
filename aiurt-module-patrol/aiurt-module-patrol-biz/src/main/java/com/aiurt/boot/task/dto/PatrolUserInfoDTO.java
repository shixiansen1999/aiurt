package com.aiurt.boot.task.dto;

import com.aiurt.boot.task.entity.PatrolTaskUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cgkj0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolUserInfoDTO extends PatrolTaskUser {

    /**
     * 组织机构编码
     */
    @ApiModelProperty(value = "组织机构编码")
    private java.lang.String orgCode;
    /**
     * 组织机构名称
     */
    @ApiModelProperty(value = "组织机构名称")
    private java.lang.String orgName;
}
