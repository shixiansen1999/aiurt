package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.mapper.SysFileManageMapper;
import com.aiurt.modules.sysfile.mapper.SysFolderFilePermissionMapper;
import com.aiurt.modules.sysfile.mapper.SysFolderMapper;
import com.aiurt.modules.sysfile.param.SysFolderFilePermissionParam;
import com.aiurt.modules.sysfile.service.ISysFileManageService;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Resource
    private SysFileManageMapper sysFileManageMapper;
    @Resource
    private SysFolderMapper sysFolderMapper;

    @Override
    public void updateFolderFilePermission(Long folderId, Long fileId, List<SysFolderFilePermissionParam> sysFolderFilePermissionParams) {
        if (CollUtil.isEmpty(sysFolderFilePermissionParams)) {
            return;
        }

        List<SysFolderFilePermission> result = new ArrayList<>();

        for (SysFolderFilePermissionParam sysFolderFilePermissionParam : sysFolderFilePermissionParams) {
            if (ObjectUtil.isEmpty(sysFolderFilePermissionParam)) {
                continue;
            }

            Integer permission = sysFolderFilePermissionParam.getPermission();
            List<String> orgCodes = Optional.ofNullable(sysFolderFilePermissionParam.getOrgCodes()).orElse(CollUtil.newArrayList()).stream().distinct().collect(Collectors.toList());
            List<String> userIds = Optional.ofNullable(sysFolderFilePermissionParam.getUserIds()).orElse(CollUtil.newArrayList()).stream().distinct().collect(Collectors.toList());

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

    @Override
    public void saveSysFolderFilePermission() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }

        List<SysFile> sysFileList = sysFileManageMapper.selectList(null);
        List<SysFileType> sysFileTypeList = sysFolderMapper.selectList(null);

        List<SysFolderFilePermission> saveData = new ArrayList<>();
        if (CollUtil.isNotEmpty(sysFileList)) {
            for (SysFile sysFile : sysFileList) {
                SysFolderFilePermission sysFolderFilePermission = new SysFolderFilePermission();
                sysFolderFilePermission.setFileId(sysFile.getId());
                sysFolderFilePermission.setUserId(loginUser.getId());
                sysFolderFilePermission.setPermission(SysFileConstant.PERMISSION_MANAGE);
                saveData.add(sysFolderFilePermission);
            }
        }

        if (CollUtil.isNotEmpty(sysFileTypeList)) {
            for (SysFileType sysFileType : sysFileTypeList) {
                SysFolderFilePermission sysFolderFilePermission = new SysFolderFilePermission();
                sysFolderFilePermission.setFolderId(sysFileType.getId());
                sysFolderFilePermission.setUserId(loginUser.getId());
                sysFolderFilePermission.setPermission(SysFileConstant.PERMISSION_MANAGE);
                saveData.add(sysFolderFilePermission);
            }
        }

        this.saveBatch(saveData, 500);
    }
}
