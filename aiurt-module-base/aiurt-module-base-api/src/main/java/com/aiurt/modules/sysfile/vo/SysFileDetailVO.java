package com.aiurt.modules.sysfile.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: SysFileDetailVO
 * @author: wgp
 * @date: 2023/05/25 9:34
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;
    /**
     * 类型id
     */
    @ApiModelProperty(value = "类型id")
    private Long typeId;
    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String name;
    /**
     * 文件url
     */
    @ApiModelProperty(value = "文件url")
    private String url;
    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id")
    private String createUserId;

    /**
     * 创建人名称
     */
    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    /**
     * 权限信息
     */
    @ApiModelProperty(value = "权限信息")
    private List<SysFolderFilePermissionVO> sysFolderFilePermissionList;
}
