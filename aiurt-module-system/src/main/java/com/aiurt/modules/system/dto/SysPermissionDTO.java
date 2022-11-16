package com.aiurt.modules.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 批量启停菜单dto
 * @Author
 * @Date 2022/11/16 10:04
 **/
@Data
public class SysPermissionDTO {
    @ApiModelProperty("菜单id")
    private String id;
    @ApiModelProperty("是否停用（false否,true是）")
    private Boolean hidden;
}
