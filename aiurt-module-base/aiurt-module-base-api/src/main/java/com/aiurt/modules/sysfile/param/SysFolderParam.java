package com.aiurt.modules.sysfile.param;

import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: SysFolderParam是表示系统文件夹参数的类。
 * @author: wgp
 * @date: 2023/05/23 9:16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFolderParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件夹id")
    private Long id;

    @ApiModelProperty("上级菜单")
    @NotNull(message = "上级菜单是不能为空")
    private Long parentId;

    @ApiModelProperty("文件夹名称")
    @NotBlank(message = "文件夹名称是不能为空")
    private String name;

    @ApiModelProperty("文件夹等级")
    @NotNull(message = "文件夹等级标识是不能为空的哦")
    private Integer grade;

    @ApiModelProperty("权限信息")
    private List<SysFolderFilePermissionParam> sysFolderFilePermissionParams;
}
