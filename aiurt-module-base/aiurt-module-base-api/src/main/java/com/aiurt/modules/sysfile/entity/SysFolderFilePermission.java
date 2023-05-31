package com.aiurt.modules.sysfile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author:wgp
 * @create: 2023-05-22 17:50
 * @Description: 文件权限表
 */
@Data
@TableName("sys_folder_file_permission")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysFolderFilePermission", description = "SysFolderFilePermission")
public class SysFolderFilePermission {
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "权限ID")
    private String id;

    @ApiModelProperty(value = "组织机构编码")
    private String orgCode;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "文件ID，对应sys_file的ID")
    private Long fileId;

    @ApiModelProperty(value = "文件夹ID，对应sys_file_type的id")
    private Long folderId;

    @ApiModelProperty(value = "权限（1允许查看，2允许下载、3允许在线编辑、4允许删除、5允许编辑、6可管理权限）")
    private Integer permission;

    @ApiModelProperty(value = "删除状态")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
