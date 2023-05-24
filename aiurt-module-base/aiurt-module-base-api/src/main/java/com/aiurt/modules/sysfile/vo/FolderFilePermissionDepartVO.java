package com.aiurt.modules.sysfile.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author:wgp
 * @create: 2023-05-24 16:51
 * @Description: 文件夹或文件对应的部门权限
 */
@Data
@Accessors(chain = true)
public class FolderFilePermissionDepartVO {
    /**机构/部门名称*/
    @ApiModelProperty(value = "机构/部门名称")
    private String departName;

    /**机构编码*/
    @ApiModelProperty(value = "机构编码")
    private String orgCode;
}
