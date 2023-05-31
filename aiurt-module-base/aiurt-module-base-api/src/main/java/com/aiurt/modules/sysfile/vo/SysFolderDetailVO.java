package com.aiurt.modules.sysfile.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * @description: SysFileTypeDetailVO
 * @author: Mr.zhao
 * @date: 2021/10/29 9:34
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFolderDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**
     * 文件夹名称
     */
    @Excel(name = "文件夹名称", width = 15)
    @ApiModelProperty(value = "文件夹名称")
    private String name;

    /**
     * 创建人id
     */
    @Excel(name = "创建人id", width = 15)
    @ApiModelProperty(value = "创建人id")
    private String createUserId;

    /**
     * 创建人名称
     */
    @Excel(name = "创建人名称", width = 15)
    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    /**
     * 权限信息
     */
    @ApiModelProperty(value = "权限信息")
    private List<SysFolderFilePermissionVO> sysFolderFilePermissionList;
}
