package com.aiurt.modules.sysfile.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: SysFileParam是表示系统文件参数的类。
 * @author: wgp
 * @date: 2023/05/23 9:16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件id")
    private Long id;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "大小")
    private String fileSize;

    @ApiModelProperty(value = "文件url")
    @NotBlank(message = "文件url不能为空")
    private String url;

    @ApiModelProperty("文件夹id")
    @NotNull(message = "文件夹id不能为空")
    private Long typeId;

    @NotEmpty(message = "上传人员不能为空")
    private String uploadUserId;

    @ApiModelProperty("权限信息")
    private List<SysFolderFilePermissionParam> sysFolderFilePermissionParams;
}
