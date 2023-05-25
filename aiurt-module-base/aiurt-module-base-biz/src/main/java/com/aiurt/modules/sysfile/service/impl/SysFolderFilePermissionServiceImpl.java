package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.mapper.SysFolderFilePermissionMapper;
import com.aiurt.modules.sysfile.param.SysFolderFilePermissionParam;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 文件夹与文件的权限实现类
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
@Service
@Slf4j
public class SysFolderFilePermissionServiceImpl extends ServiceImpl<SysFolderFilePermissionMapper, SysFolderFilePermission> implements ISysFolderFilePermissionService {

    @Override
    public void updateFolderFilePermission(Long folderId, Long fileId, List<SysFolderFilePermissionParam> sysFolderFilePermissionParams) {
        if (CollUtil.isEmpty(sysFolderFilePermissionParams)) {
            return;
        }

        List<SysFolderFilePermission> result = new ArrayList<>();
        SysFolderFilePermission sysFolderFilePermission = null;

        for (SysFolderFilePermissionParam sysFolderFilePermissionParam : sysFolderFilePermissionParams) {
            if (ObjectUtil.isEmpty(sysFolderFilePermission)) {
                continue;
            }

            Integer permission = sysFolderFilePermissionParam.getPermission();
            List<String> orgCodes = sysFolderFilePermissionParam.getOrgCodes();
            List<String> userIds = sysFolderFilePermissionParam.getUserIds();

            if (CollUtil.isNotEmpty(orgCodes)) {
                List<SysFolderFilePermission> sysFolderFilePermissions = orgCodes.stream().distinct().map(
                        orgCode -> new SysFolderFilePermission()
                                .setFolderId(folderId)
                                .setPermission(permission)
                                .setOrgCode(orgCode)
                                .setFileId(fileId)
                ).collect(Collectors.toList());
                result.addAll(sysFolderFilePermissions);
            }

            if (CollUtil.isNotEmpty(userIds)) {
                List<SysFolderFilePermission> sysFolderFilePermissions = userIds.stream().distinct().map(
                        userId -> new SysFolderFilePermission()
                                .setFolderId(folderId)
                                .setPermission(permission)
                                .setUserId(userId)
                                .setFileId(fileId)
                ).collect(Collectors.toList());
                result.addAll(sysFolderFilePermissions);
            }
        }

        this.saveBatch(result, 500);
    }


}
