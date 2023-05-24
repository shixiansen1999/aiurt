package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.FillRuleConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.FillRuleUtil;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileRole;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.mapper.SysFolderMapper;
import com.aiurt.modules.sysfile.param.SysFolderParam;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.aiurt.modules.sysfile.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: 文件夹实现类
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
@Service
@Slf4j
public class SysFolderServiceImpl extends ServiceImpl<SysFolderMapper, SysFileType> implements ISysFolderService {

    @Resource
    private SysFolderMapper sysFolderMapper;
    @Resource
    private ISysFolderFilePermissionService sysFolderFilePermissionService;
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void addFolder(HttpServletRequest req, SysFolderParam param) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }

        SysFileType type = new SysFileType();

        checkDuplicateFolder(param);

        validateFolderName(param.getName().trim());

        setFolderParameters(type, param, loginUser.getUsername());

        if (!this.save(type)) {
            throw new AiurtBootException("添加文件夹未成功,请稍后重试");
        }

        this.saveFolderFilePermission(type);
    }


    @Override
    public List<SysFolderTreeVO> queryFolderTree(String name, Long parentId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }

        if (parentId != null) {
            parentId = SysFileConstant.NUM_LONG_0;
        }

        List<SysFolderTreeVO> result = sysFolderMapper.queryFolderTree(name, parentId, loginUser.getId(), loginUser.getOrgCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(HttpServletRequest req, SysFolderParam param) {
        SysFileType sysFileType = this.getById(param.getId());
        if (ObjectUtil.isEmpty(sysFileType)) {
            throw new AiurtBootException("未查询到此项数据");
        }

        validateFolderName(param.getName().trim());

        // 修改文件夹
        sysFileType
                .setParentId(param.getParentId())
                .setGrade(param.getGrade())
                .setName(param.getName().trim());
        boolean result = this.updateById(sysFileType);

        if (result) {
            if (CollUtil.isNotEmpty(param.getAddSysFolderFilePermissionList())) {
                Date currDate = new Date();
                List<SysFolderFilePermission> sysFolderFilePermissions = param.getAddSysFolderFilePermissionList().stream()
                        .map(p -> {
                            p.setIsExtends(false);
                            p.setFolderId(sysFileType.getId());
                            p.setCreateTime(currDate);
                            return p;
                        }).collect(Collectors.toList());
                sysFolderFilePermissionService.saveBatch(sysFolderFilePermissions, 500);
            }

            if (CollUtil.isNotEmpty(param.getDeleteSysFolderFilePermissionParams())) {
                List<String> deleteSysFolderFilePermissionIds = param.getDeleteSysFolderFilePermissionParams().stream().map(SysFolderFilePermission::getId).collect(Collectors.toList());
                sysFolderFilePermissionService.removeBatchByIds(deleteSysFolderFilePermissionIds, 500);
            }
        }

    }

    @Override
    public SysFolderDetailVO detail(HttpServletRequest req, Long id) {
        SysFileType sysFileType = this.getById(id);
        if (ObjectUtil.isEmpty(sysFileType)) {
            throw new AiurtBootException("未查询到此条记录");
        }

        SysFolderDetailVO result = new SysFolderDetailVO();
        BeanUtils.copyProperties(sysFileType, result);

        // 查询文件夹创建人的信息
        LoginUser createUser = sysBaseApi.queryUser(sysFileType.getCreateBy());
        result.setCreateUserId(ObjectUtil.isNotEmpty(createUser) ? createUser.getId() : "");
        result.setCreateUserName(ObjectUtil.isNotEmpty(createUser) ? createUser.getRealname() : "");

        // 查询文件夹权限
        List<SysFolderFilePermission> sysFolderFilePermissions = sysFolderFilePermissionService.list(
                new LambdaQueryWrapper<SysFolderFilePermission>()
                        .select(SysFolderFilePermission::getOrgCode)
                        .select(SysFolderFilePermission::getUserId)
                        .eq(SysFolderFilePermission::getFolderId, sysFileType.getId())
                        .eq(SysFolderFilePermission::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(sysFolderFilePermissions)) {
            // 获取文件夹的用户ID列表，并去重
            String[] userIdArray = sysFolderFilePermissions.stream()
                    .map(SysFolderFilePermission::getUserId)
                    .distinct()
                    .toArray(String[]::new);

            // 查询所有用户信息，并根据用户ID列表进行过滤和转换
            List<SimpUserVO> simpUserList = Optional.ofNullable(sysBaseApi.queryAllUserByIds(userIdArray))
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(user -> user.getId() != null)
                    .map(user -> new SimpUserVO().setUserId(user.getId()).setUserName(user.getRealname()))
                    .collect(Collectors.toList());

            // 获取文件夹的组织机构代码列表，并去重
            String orgCodeStr = sysFolderFilePermissions.stream()
                    .map(SysFolderFilePermission::getOrgCode)
                    .distinct()
                    .collect(Collectors.joining(","));

            // 查询所有组织机构信息，并根据组织机构代码列表进行过滤和转换
            List<FolderFilePermissionDepartVO> folderFilePermissionDepartList = Optional.ofNullable(sysBaseApi.queryDepartsByOrgcodes(orgCodeStr))
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(depart -> depart.get("orgCode") != null && depart.get("departName") != null)
                    .map(depart -> new FolderFilePermissionDepartVO().setOrgCode(String.valueOf(depart.get("orgCode"))).setDepartName(String.valueOf(depart.get("departName"))))
                    .collect(Collectors.toList());

            // 构建权限详情列表
            List<SysFolderFilePermissionVO> sysFolderFilePermissionList = buildPermissionDetails(sysFolderFilePermissions, simpUserList, folderFilePermissionDepartList);

            result.setSysFolderFilePermissionList(sysFolderFilePermissionList);
        }
        return result;
    }

    @Override
    public void deleteFolder(HttpServletRequest req, List<Long> ids) {
        LambdaQueryWrapper<SysFileType> lam = new LambdaQueryWrapper();
        List<Long> folderIds = this.list(lam).stream().map(SysFileType::getId).collect(Collectors.toList());
//        SysFileType one = this.sysFileTypeService.lambdaQuery().in(SysFileType::getParentId, ids).last("limit 1").one();
//        if (one!=null){
//            throw new AiurtBootException("此目录下有文件夹,无法被直接删除");
//        }
//
//        if (!this.sysFileTypeService.removeByIds(ids)){
//            return Result.error("删除失败!");
//        }
//        //为了清空文件和权限所作操作,并不一定有数据,不做判断
//        this.sysFileService.lambdaUpdate().in(SysFile::getTypeId,ids).remove();
//        this.sysFileRoleService.lambdaUpdate().in(SysFileRole::getTypeId, ids).remove();

//        return Result.ok("删除成功!");
    }

    /**
     * 保存文件夹权限
     *
     * @param type SysFileType对象，要保存文件权限的文件夹对象
     */
    private void saveFolderFilePermission(SysFileType type) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }
        List<SysFolderFilePermission> folderPermission = new ArrayList<>();

        // 如果文件夹有父节点，则继承父节点的权限
        if (type.getParentId() != null) {
            // 获取父节点的文件权限
            LambdaQueryWrapper<SysFolderFilePermission> queryWrapper = new LambdaQueryWrapper<SysFolderFilePermission>()
                    .select(SysFolderFilePermission::getFolderId)
                    .select(SysFolderFilePermission::getOrgCode)
                    .select(SysFolderFilePermission::getUserId)
                    .select(SysFolderFilePermission::getDelFlag)
                    .select(SysFolderFilePermission::getPermission)
                    .eq(SysFolderFilePermission::getFolderId, type.getParentId())
                    .eq(SysFolderFilePermission::getDelFlag, CommonConstant.DEL_FLAG_0);

            folderPermission = sysFolderFilePermissionService.list(queryWrapper);

            if (CollUtil.isNotEmpty(folderPermission)) {
                folderPermission.forEach(p -> {
                    p.setFolderId(type.getId());
                    p.setIsExtends(true);
                });
            }
        } else {
            SysFolderFilePermission sysFolderFilePermission = new SysFolderFilePermission();
            sysFolderFilePermission.setFolderId(type.getId());
            sysFolderFilePermission.setUserId(loginUser.getId());
            sysFolderFilePermission.setPermission(SysFileConstant.PERMISSION_MANAGE);
            sysFolderFilePermission.setIsExtends(false);
            folderPermission.add(sysFolderFilePermission);
        }

        sysFolderFilePermissionService.saveBatch(folderPermission, 500);
    }

    /**
     * 检查同级是否存在同名文件夹，并抛出异常
     *
     * @param param SysFileTypeParam对象，包含文件夹的参数信息
     */
    private void checkDuplicateFolder(SysFolderParam param) {
        boolean exists = sysFolderMapper.exists(new LambdaQueryWrapper<SysFileType>()
                .eq(SysFileType::getGrade, param.getGrade())
                .eq(SysFileType::getName, param.getName())
                .eq(SysFileType::getParentId, param.getParentId()));

        if (exists) {
            throw new AiurtBootException("添加文件夹未成功，同级已添加同名的文件夹");
        }
    }

    /**
     * 设置文件夹参数值
     *
     * @param type     SysFileType对象，要设置参数的文件夹对象
     * @param param    SysFileTypeParam对象，包含文件夹的参数信息
     * @param username 当前登录名称的账号
     */
    private void setFolderParameters(SysFileType type, SysFolderParam param, String username) {
        Long parentId = param.getParentId() != null ? param.getParentId() : SysFileConstant.NUM_LONG_0;
        type.setGrade(param.getGrade())
                .setName(param.getName())
                .setDelFlag(CommonConstant.DEL_FLAG_0)
                .setParentId(parentId)
                .setCreateTime(new Date())
                .setCreateBy(username);

        // 生成编码并设置层级结构
        generateFolderCode(type, parentId);
    }

    /**
     * 校验文件夹名称是否包含特殊字符
     *
     * @param folderName 文件夹名称
     * @return true表示文件夹名称不包含特殊字符，false表示文件夹名称包含特殊字符
     */
    public void validateFolderName(String folderName) {
        // 定义特殊字符的正则表达式
        String specialChars = "[!@#$%^&*()_+\\[\\]{};':\"|<>?/.,]";

        // 使用正则表达式匹配文件夹名称
        boolean hasSpecialChars = folderName.matches("." + specialChars + ".");
        if (!hasSpecialChars) {
            throw new AiurtBootException("名称不得包含" + specialChars);
        }
    }

    /**
     * 生成文件夹编码并设置Code和CodeCc字段
     *
     * @param type     SysFileType对象，要设置编码的文件夹对象
     * @param parentId 上级文件夹的ID，可以为null或空字符串
     */
    private void generateFolderCode(SysFileType type, Long parentId) {
        JSONObject formData = new JSONObject();
        formData.put("parentId", parentId);

        // 执行填充规则，生成编码数组
        String[] codeArray = (String[]) FillRuleUtil.executeRule(FillRuleConstant.DEPART, formData);
        type.setFolderCode(codeArray[0]);

        if (parentId == null) {
            // 如果上级文件夹为空，则设置CodeCc为"/" + Code + "/"
            type.setFolderCodeCc("/" + type.getFolderCode() + "/");
        } else {
            // 查询上级文件夹的编码Cc
            SysFileType fileType = baseMapper.selectById(parentId);
            if (Objects.nonNull(fileType) && StrUtil.isNotBlank(fileType.getFolderCodeCc())) {
                // 如果上级文件夹存在且编码Cc不为空，则设置CodeCc为上级文件夹的编码Cc + Code + "/"
                type.setFolderCodeCc(fileType.getFolderCodeCc() + type.getFolderCode() + "/");
            }
        }
    }

    /**
     * 构建文件夹权限详情列表
     *
     * @param sysFolderFilePermissions       文件夹权限列表
     * @param simpUserList                   用户列表
     * @param folderFilePermissionDepartList 组织机构列表
     * @return 文件夹权限详情列表
     */
    private List<SysFolderFilePermissionVO> buildPermissionDetails(List<SysFolderFilePermission> sysFolderFilePermissions,
                                                                   List<SimpUserVO> simpUserList,
                                                                   List<FolderFilePermissionDepartVO> folderFilePermissionDepartList) {
        // 根据权限进行分组，构建权限列表
        Map<Integer, List<SysFolderFilePermission>> folderFilePermissionMap = sysFolderFilePermissions.stream()
                .collect(Collectors.groupingBy(SysFolderFilePermission::getPermission));

        List<SysFolderFilePermissionVO> sysFolderFilePermissionList = new ArrayList<>();

        // 遍历权限列表，构建权限详情信息
        folderFilePermissionMap.forEach((permission, permissionList) -> {
            if (CollUtil.isNotEmpty(permissionList)) {
                SysFolderFilePermissionVO sysFolderFilePermissionVO = new SysFolderFilePermissionVO();
                sysFolderFilePermissionVO.setPermission(permission);

                List<String> userIdList = permissionList.stream().map(SysFolderFilePermission::getUserId).collect(Collectors.toList());
                List<SimpUserVO> selectedUsers = simpUserList.stream()
                        .filter(user -> userIdList.contains(user.getUserId()))
                        .collect(Collectors.toList());
                sysFolderFilePermissionVO.setUsers(selectedUsers);

                List<String> orgCodeList = permissionList.stream().map(SysFolderFilePermission::getOrgCode).collect(Collectors.toList());
                List<FolderFilePermissionDepartVO> selectedDeparts = folderFilePermissionDepartList.stream()
                        .filter(depart -> orgCodeList.contains(depart.getOrgCode()))
                        .collect(Collectors.toList());
                sysFolderFilePermissionVO.setOrgCodes(selectedDeparts);
                sysFolderFilePermissionList.add(sysFolderFilePermissionVO);
            }
        });

        return sysFolderFilePermissionList;
    }
}
