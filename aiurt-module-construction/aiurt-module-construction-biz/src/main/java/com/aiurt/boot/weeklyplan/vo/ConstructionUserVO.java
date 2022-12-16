package com.aiurt.boot.weeklyplan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 施工负责人VO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionUserVO {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;
}
