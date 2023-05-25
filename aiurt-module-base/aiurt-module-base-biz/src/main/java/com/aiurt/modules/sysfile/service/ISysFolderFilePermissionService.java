package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.param.SysFolderFilePermissionParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 文件夹与文件的权限接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface ISysFolderFilePermissionService extends IService<SysFolderFilePermission> {

    /**
     * 更新文件夹权限
     *
     * @param folderId                      文件夹ID
     * @param fileId                        文件ID
     * @param sysFolderFilePermissionParams 文件夹权限参数列表
     */
    void updateFolderFilePermission(Long folderId, Long fileId, List<SysFolderFilePermissionParam> sysFolderFilePermissionParams);
}
