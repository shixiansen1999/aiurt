package com.aiurt.boot.modules.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/26 15:54
 */
@Data
public class AppUserVO {
    @ApiModelProperty(value = "账号")
    @NotNull(message = "用户名不能为空")
    private String userName;
    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    private String password;
}
