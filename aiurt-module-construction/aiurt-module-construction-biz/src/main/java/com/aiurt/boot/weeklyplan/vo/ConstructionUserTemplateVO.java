package com.aiurt.boot.weeklyplan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 施工供电模板VO对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ConstructionUserTemplateVO对象", description = "ConstructionUserTemplateVO对象")
public class ConstructionUserTemplateVO {
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户token")
    private String token;


}
