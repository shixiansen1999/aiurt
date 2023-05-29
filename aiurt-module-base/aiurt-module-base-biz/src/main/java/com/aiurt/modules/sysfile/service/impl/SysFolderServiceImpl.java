package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.FillRuleConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.FillRuleUtil;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.mapper.SysFolderMapper;
import com.aiurt.modules.sysfile.param.SysFolderParam;
import com.aiurt.modules.sysfile.service.ISysFileService;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.aiurt.modules.sysfile.utils.FileNameUtils;
import com.aiurt.modules.sysfile.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private ISysFileService sysFileService;
    @Resource
    private SysFolderMapper sysFolderMapper;
    @Resource
    private ISysFolderFilePermissionService sysFolderFilePermissionService;
    @Resource
    private ISysBaseAPI sysBaseApi;

    private final Lock lock = new ReentrantLock();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void addFolder(HttpServletRequest req, SysFolderParam param) {
        LoginUser loginUser = getLoginUser();

        SysFileType type = new SysFileType();

        checkDuplicateFolder(param);

        FileNameUtils.validateFolderName(param.getName().trim());

        setFolderParameters(type, param, loginUser.getUsername());

        if (!this.save(type)) {
            throw new AiurtBootException("添加文件夹未成功,请稍后重试");
        }

        this.saveFolderFilePermission(type);
    }

    @Override
    public List<SysFolderTreeVO> queryFolderTree(String name, Long pid) {
        LoginUser loginUser = getLoginUser();

        if (pid == null) {
            pid = SysFileConstant.NUM_LONG_0;
        }

        List<SysFolderTreeVO> result = sysFolderMapper.queryFolderTree(name, pid, loginUser.getId(), loginUser.getOrgCode());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(HttpServletRequest req, SysFolderParam param) {
        SysFileType sysFileType = this.getById(param.getId());
        if (ObjectUtil.isEmpty(sysFileType)) {
            throw new AiurtBootException("未查询到此项数据");
        }

        try {
            FileNameUtils.validateFolderName(param.getName().trim());

            // 修改文件夹基本信息
            boolean isUpdateSuccess = updateFileType(sysFileType, param);

            // 删除原来的权限信息
            boolean isDeleteSuccess = deleteOriginalPermissions(sysFileType);

            // 新增权限信息
            if (isUpdateSuccess && isDeleteSuccess) {
                sysFolderFilePermissionService.updateFolderFilePermission(sysFileType.getId(), null, param.getSysFolderFilePermissionParams());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AiurtBootException("编辑失败");
        }
    }

    @Override
    public SysFolderDetailVO detail(HttpServletRequest req, Long id) {
        SysFileType sysFileType = this.getById(id);
        if (ObjectUtil.isEmpty(sysFileType)) {
            throw new AiurtBootException("未查询到此项数据");
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
                        .select(SysFolderFilePermission::getOrgCode,SysFolderFilePermission::getUserId,SysFolderFilePermission::getPermission)
                        .eq(SysFolderFilePermission::getFolderId, sysFileType.getId())
                        .eq(SysFolderFilePermission::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(sysFolderFilePermissions)) {
            List<SysFolderFilePermissionVO> sysFolderFilePermissionList = getPermissionDetails(sysFolderFilePermissions);
            result.setSysFolderFilePermissionList(sysFolderFilePermissionList);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFolder(HttpServletRequest req, List<Long> ids) {
        LambdaQueryWrapper<SysFileType> lam = new LambdaQueryWrapper();
        lam.in(SysFileType::getParentId, ids).last("limit 1");
        long count = this.count(lam);

        if (count > 0) {
            throw new AiurtBootException("此目录下有文件夹,无法被直接删除");
        }

        boolean isDeleteSuccess = this.removeBatchByIds(ids, 500);
        if (!isDeleteSuccess) {
            throw new AiurtBootException("删除失败!");
        }

        // 为了清空文件和权限所作操作,并不一定有数据,不做判断
        sysFileService.lambdaUpdate().in(SysFile::getTypeId, ids).remove();
        sysFolderFilePermissionService.lambdaUpdate().in(SysFolderFilePermission::getFolderId, ids).remove();
    }

    /**
     * 保存文件夹权限
     *
     * @param type SysFileType对象，要保存文件权限的文件夹对象
     */
    private void saveFolderFilePermission(SysFileType type) {
        LoginUser loginUser = getLoginUser();
        List<SysFolderFilePermission> folderPermission = new ArrayList<>();

        // 如果文件夹有父节点，则继承父节点的权限
        if (type.getParentId() != null && !SysFileConstant.NUM_LONG_0.equals(type.getParentId())) {
            // 获取父节点的文件权限
            LambdaQueryWrapper<SysFolderFilePermission> queryWrapper = new LambdaQueryWrapper<SysFolderFilePermission>()
                    .select(SysFolderFilePermission::getFolderId, SysFolderFilePermission::getOrgCode, SysFolderFilePermission::getUserId, SysFolderFilePermission::getDelFlag, SysFolderFilePermission::getPermission)
                    .eq(SysFolderFilePermission::getFolderId, type.getParentId())
                    .eq(SysFolderFilePermission::getDelFlag, CommonConstant.DEL_FLAG_0);

            folderPermission = sysFolderFilePermissionService.list(queryWrapper);

            if (CollUtil.isNotEmpty(folderPermission)) {
                folderPermission.forEach(p -> p.setFolderId(type.getId()));
            }
        } else {
            SysFolderFilePermission sysFolderFilePermission = new SysFolderFilePermission();
            sysFolderFilePermission.setFolderId(type.getId());
            sysFolderFilePermission.setUserId(loginUser.getId());
            sysFolderFilePermission.setPermission(SysFileConstant.PERMISSION_MANAGE);
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
        boolean exists = sysFolderMapper.exists(new LambdaQueryWrapper<SysFileType>().eq(SysFileType::getGrade, param.getGrade()).eq(SysFileType::getName, param.getName()).eq(SysFileType::getParentId, param.getParentId()));

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
     * 生成文件夹编码并设置Code和CodeCc字段
     *
     * @param type     SysFileType对象，要设置编码的文件夹对象
     * @param parentId 上级文件夹的ID，可以为null或空字符串
     */
    private void generateFolderCode(SysFileType type, Long parentId) {
        JSONObject formData = new JSONObject();
        formData.put("parentId", parentId);

        // 执行填充规则，生成编码数组
        String[] codeArray = (String[]) FillRuleUtil.executeRule(FillRuleConstant.FOLDER_NUM_CODE, formData);
        type.setFolderCode(codeArray[0]);

        if (SysFileConstant.NUM_LONG_0.equals(parentId)) {
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
    private List<SysFolderFilePermissionVO> buildPermissionDetails(List<SysFolderFilePermission> sysFolderFilePermissions, List<SimpUserVO> simpUserList, List<FolderFilePermissionDepartVO> folderFilePermissionDepartList) {
        // 根据权限进行分组，构建权限列表
        Map<Integer, List<SysFolderFilePermission>> folderFilePermissionMap = sysFolderFilePermissions.stream().collect(Collectors.groupingBy(SysFolderFilePermission::getPermission));

        List<SysFolderFilePermissionVO> sysFolderFilePermissionList = new ArrayList<>();

        // 遍历权限列表，构建权限详情信息
        if (MapUtil.isEmpty(folderFilePermissionMap)) {
            return sysFolderFilePermissionList;
        }
        folderFilePermissionMap.forEach((permission, permissionList) -> {
            if (CollUtil.isNotEmpty(permissionList)) {
                SysFolderFilePermissionVO sysFolderFilePermissionVO = new SysFolderFilePermissionVO();
                sysFolderFilePermissionVO.setPermission(permission);

                List<String> userIdList = permissionList.stream().map(SysFolderFilePermission::getUserId).collect(Collectors.toList());
                List<SimpUserVO> selectedUsers = simpUserList.stream().filter(user -> userIdList.contains(user.getUserId())).collect(Collectors.toList());
                sysFolderFilePermissionVO.setUsers(selectedUsers);

                List<String> orgCodeList = permissionList.stream().map(SysFolderFilePermission::getOrgCode).collect(Collectors.toList());
                List<FolderFilePermissionDepartVO> selectedDeparts = folderFilePermissionDepartList.stream().filter(depart -> orgCodeList.contains(depart.getOrgCode())).collect(Collectors.toList());
                sysFolderFilePermissionVO.setOrgCodes(selectedDeparts);
                sysFolderFilePermissionList.add(sysFolderFilePermissionVO);
            }
        });

        return sysFolderFilePermissionList;
    }

    /**
     * 删除原来的权限信息
     *
     * @param sysFileType 文件类型对象
     */
    public boolean deleteOriginalPermissions(SysFileType sysFileType) {
        // 构造查询条件
        LambdaQueryWrapper<SysFolderFilePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysFolderFilePermission::getId);
        lambdaQueryWrapper.eq(SysFolderFilePermission::getFolderId, sysFileType.getId());

        // 查询需要删除的权限信息
        List<SysFolderFilePermission> sysFolderFilePermissions = sysFolderFilePermissionService.list(lambdaQueryWrapper);

        // 提取权限ID列表
        List<String> permissionIds = Optional.ofNullable(sysFolderFilePermissions).orElse(Collections.emptyList()).stream().map(SysFolderFilePermission::getId).collect(Collectors.toList());

        // 批量删除权限信息
        return sysFolderFilePermissionService.removeBatchByIds(permissionIds, 500);
    }

    /**
     * 更新文件类型信息
     *
     * @param sysFileType 文件类型对象
     * @param param       更新参数对象
     * @return true表示更新成功，false表示更新失败
     */
    public boolean updateFileType(SysFileType sysFileType, SysFolderParam param) {
        sysFileType.setParentId(param.getParentId());
        sysFileType.setGrade(param.getGrade());
        sysFileType.setName(param.getName().trim());
        sysFileType.setUpdateTime(new Date());
        sysFileType.setUpdateBy(getLoginUser().getUsername());

        return this.updateById(sysFileType);
    }

    /**
     * 获取文件夹的权限详情列表
     *
     * @param sysFolderFilePermissions 文件夹文件权限列表
     * @return 权限详情列表
     */
    @Override
    public List<SysFolderFilePermissionVO> getPermissionDetails(List<SysFolderFilePermission> sysFolderFilePermissions) {
        // 获取文件夹的用户ID列表，并去重
        String[] userIdArray = Optional.ofNullable(sysFolderFilePermissions).orElse(Collections.emptyList()).stream()
                .map(SysFolderFilePermission::getUserId)
                .distinct()
                .toArray(String[]::new);

        // 查询所有用户信息，并根据用户ID列表进行过滤和转换
        List<SimpUserVO> simpUserList = Optional.ofNullable(sysBaseApi.queryAllUserByIds(userIdArray)).orElse(Collections.emptyList()).stream()
                .filter(user -> user.getId() != null)
                .map(user -> new SimpUserVO().setUserId(user.getId()).setUserName(user.getRealname()))
                .collect(Collectors.toList());

        // 获取文件夹的组织机构代码列表，并去重
        String orgCodeStr = Optional.ofNullable(sysFolderFilePermissions).orElse(Collections.emptyList()).stream()
                .map(SysFolderFilePermission::getOrgCode)
                .distinct()
                .collect(Collectors.joining(","));

        // 查询所有组织机构信息，并根据组织机构代码列表进行过滤和转换
        List<FolderFilePermissionDepartVO> folderFilePermissionDepartList = Optional.ofNullable(sysBaseApi.queryDepartsByOrgcodes(orgCodeStr)).orElse(Collections.emptyList()).stream()
                .filter(depart -> depart.get("orgCode") != null && depart.get("departName") != null)
                .map(depart -> new FolderFilePermissionDepartVO()
                        .setOrgCode(String.valueOf(depart.get("orgCode")))
                        .setDepartName(String.valueOf(depart.get("departName"))))
                .collect(Collectors.toList());

        // 构建权限详情列表
        List<SysFolderFilePermissionVO> sysFolderFilePermissionList = buildPermissionDetails(sysFolderFilePermissions, simpUserList, folderFilePermissionDepartList);

        return sysFolderFilePermissionList;
    }

    @Override
    public Map<Long, List<SysFolderFilePermission>> getPermissionByFolderId(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashMap<>(8);
        }

        // 继承文件夹的权限
        LambdaQueryWrapper<SysFolderFilePermission> lam = new LambdaQueryWrapper<>();
        lam.select(SysFolderFilePermission::getUserId, SysFolderFilePermission::getOrgCode, SysFolderFilePermission::getFolderId, SysFolderFilePermission::getPermission);
        lam.in(SysFolderFilePermission::getFolderId, ids);
        List<SysFolderFilePermission> sysFolderFilePermissions = sysFolderFilePermissionService.list(lam);

        if (CollUtil.isNotEmpty(sysFolderFilePermissions)) {
            return sysFolderFilePermissions.stream().collect(Collectors.groupingBy(SysFolderFilePermission::getFolderId));
        }

        return new HashMap<>(8);
    }

    @Override
    public void buildData() {
        List<SysFileType> sysFileTypes = sysFolderMapper.selectList(null);
        for (SysFileType fileType : sysFileTypes) {
            Long parentId = fileType.getParentId();
            int grade = findGrade(sysFileTypes, parentId, 1);
            fileType.setGrade(grade);
            sysFolderMapper.updateById(fileType);
        }

        Integer maxGrade = getMaxGrade();
        if (ObjectUtil.isNotEmpty(maxGrade)) {
            for (Integer i = 1; i <= maxGrade; i++) {
                LambdaQueryWrapper<SysFileType> lam = new LambdaQueryWrapper<>();
                lam.eq(SysFileType::getDelFlag, CommonConstant.DEL_FLAG_0);
                lam.eq(SysFileType::getGrade, i);
                lam.orderByDesc(SysFileType::getGrade);

                List<SysFileType> sysFileTypeList = sysFolderMapper.selectList(lam);
                if (CollUtil.isEmpty(sysFileTypeList)) {
                    continue;
                }

                for (SysFileType sysFileType : sysFileTypeList) {
                    lock.lock(); // 加锁
                    try {
                        generateFolderCode(sysFileType, sysFileType.getParentId());
                        sysFolderMapper.updateById(sysFileType);
                    } finally {
                        lock.unlock(); // 解锁
                    }
                }
            }
        }
    }

    /**
     * 递归查找父级对象的层级次数
     *
     * @param sysFileTypes 文件类型列表
     * @param parentId     父级对象的 ID
     * @param level        当前层级次数
     * @return 父级对象的层级次数
     */
    private int findGrade(List<SysFileType> sysFileTypes, Long parentId, int level) {
        for (SysFileType fileType : sysFileTypes) {
            if (fileType.getId().equals(parentId)) {
                Long pid = fileType.getParentId();
                if (SysFileConstant.NUM_LONG_0.equals(pid)) {
                    return level + 1;
                }
                return findGrade(sysFileTypes, pid, level + 1);
            }
        }
        return 1;
    }

    /**
     * 获取sys_file_type表中grade列的最大值
     *
     * @return grade列的最大值，如果表为空则返回null
     */
    public Integer getMaxGrade() {
        LambdaQueryWrapper<SysFileType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysFileType::getGrade)
                .orderByDesc(SysFileType::getGrade)
                .last("LIMIT 1");
        SysFileType sysFileType = sysFolderMapper.selectOne(queryWrapper);
        return sysFileType != null ? sysFileType.getGrade() : null;
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     * @throws AiurtBootException 当用户未登录时抛出异常
     */
    private LoginUser getLoginUser() throws AiurtBootException {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }
        return loginUser;
    }
}
