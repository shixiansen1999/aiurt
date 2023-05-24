package com.aiurt.modules.sysfile.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author:wgp
 * @create: 2023-05-22 17:50
 * @Description: 文件权限表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysFolderFilePermissionParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限（1允许查看，2允许下载、3允许在线编辑、4允许删除、5允许编辑、6可管理权限）")
    private Integer permission;

    @ApiModelProperty("用户id集合")
    private List<String> userIds;

    @ApiModelProperty("部门code集合")
    private List<String> orgCodes;

}
