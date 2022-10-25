package com.aiurt.modules.appuser.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mudoudou
 */
@Data
public class UserParam {
    @ApiModelProperty(value = "姓名", required = false)
    private String realName;
    @ApiModelProperty(value = "性别", required = false)
    private Integer sex;
    @ApiModelProperty(value = "手机", required = false)
    private String phone;
    @ApiModelProperty(value = "头像", required = false)
    private String imgurl;
}
