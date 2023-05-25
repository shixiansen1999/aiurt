package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.EsFileAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.search.dto.FileDataDTO;
import com.aiurt.modules.sysfile.constant.PatrolConstant;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFileInfo;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.mapper.SysFileManageMapper;
import com.aiurt.modules.sysfile.param.SysFileParam;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.param.SysFolderParam;
import com.aiurt.modules.sysfile.service.ISysFileInfoService;
import com.aiurt.modules.sysfile.service.ISysFileManageService;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.aiurt.modules.sysfile.utils.FileNameUtils;
import com.aiurt.modules.sysfile.vo.SysFileDetailVO;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.vo.SysFileVO;
import com.aiurt.modules.sysfile.vo.SysFolderFilePermissionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author:wgp
 * @create: 2023-05-22 17:05
 * @Description: 文档接口处理类
 */
@Slf4j
@Service
public class SysFileManageServiceImpl extends ServiceImpl<SysFileManageMapper, SysFile> implements ISysFileManageService {
    @Resource
    private ISysFolderFilePermissionService sysFolderFilePermissionService;
    @Resource
    private SysFileManageMapper sysFileManageMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private EsFileAPI esFileApi;
    @Resource
    private ISysFolderService sysFolderService;
    @Resource
    private ISysFileInfoService sysFileInfoService;
    @Value("${jeecg.path.upload}")
    private String uploadPath;

    @Override
    public Page<SysFileManageVO> getFilePageList(Page<SysFileManageVO> page, SysFileWebParam sysFileWebParam) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }

        String currLoginUserId = loginUser.getId();

        String currLoginOrgCode = loginUser.getOrgCode();

        List<String> userNames = getUserNames(sysFileWebParam);

        List<SysFileManageVO> result = sysFileManageMapper.getFilePageList(page, sysFileWebParam, currLoginUserId, currLoginOrgCode, userNames);

        return page.setRecords(result);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<SysFile> addFile(List<SysFileParam> files) {
        if (CollUtil.isEmpty(files)) {
            throw new AiurtBootException("参数不能为空");
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }

        Result<SysFile> result = new Result<>();
        SysFile sysFile = null;


        for (SysFileParam file : files) {

            sysFile = new SysFile();
            BeanUtils.copyProperties(file, sysFile);

            // 处理文件名和类型
            processFileNameAndType(sysFile, file);

            // 处理文件大小
            processFileSize(sysFile, file);

            try {
                // 保存sysFile
                save(sysFile);

                // 创建并保存SysFolderFilePermission
                saveSysFolderFilePermission(sysFile);

                // ES更新规范规程知识库的文件数据
                SysFile finalSysFile = sysFile;
                new Thread(() -> this.saveEsDate(finalSysFile)).start();

                result.success("添加成功！");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("添加失败");
            }
        }
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editFile(SysFileParam sysFileParam) {
        SysFile sysFile = this.getById(sysFileParam.getId());
        if (ObjectUtil.isEmpty(sysFile)) {
            throw new AiurtBootException("未查询到此项数据");
        }

        FileNameUtils.validateFolderName(sysFileParam.getName().trim());

        try {
            // 修改文件夹基本信息
            boolean isUpdateSuccess = updateFile(sysFile, sysFileParam);

            // 删除原来的权限信息
            boolean isDeleteSuccess = deleteOriginalPermissions(sysFile);

            // 新增权限信息
            if (isUpdateSuccess && isDeleteSuccess) {
                sysFolderFilePermissionService.updateFolderFilePermission(null, sysFile.getId(), sysFileParam.getSysFolderFilePermissionParams());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AiurtBootException("编辑失败");
        }
    }

    @Override
    public int removeById(String id) {
        SysFile sysFile = this.getById(id);
        if (ObjectUtil.isEmpty(sysFile)) {
            throw new AiurtBootException("未查询到此项数据");
        }

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("请重新登录");
        }

        checkDeletePermission(sysFile, loginUser);

        return this.removeById(id);
    }

    @Override
    public SysFileDetailVO queryById(String id) {
        SysFile sysFile = this.getById(id);
        if (ObjectUtil.isEmpty(sysFile)) {
            throw new AiurtBootException("未查询到此项数据");
        }

        SysFileDetailVO result = new SysFileDetailVO();
        BeanUtils.copyProperties(sysFile, result);

        // 查询文件夹创建人的信息
        LoginUser createUser = sysBaseApi.queryUser(sysFile.getCreateBy());
        result.setCreateUserId(ObjectUtil.isNotEmpty(createUser) ? createUser.getId() : "");
        result.setCreateUserName(ObjectUtil.isNotEmpty(createUser) ? createUser.getRealname() : "");

        // 查询文件夹权限
        List<SysFolderFilePermission> sysFolderFilePermissions = sysFolderFilePermissionService.list(
                new LambdaQueryWrapper<SysFolderFilePermission>()
                        .select(SysFolderFilePermission::getOrgCode)
                        .select(SysFolderFilePermission::getUserId)
                        .eq(SysFolderFilePermission::getFileId, id)
                        .eq(SysFolderFilePermission::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(sysFolderFilePermissions)) {
            List<SysFolderFilePermissionVO> sysFolderFilePermissionList = sysFolderService.getPermissionDetails(sysFolderFilePermissions);
            result.setSysFolderFilePermissionList(sysFolderFilePermissionList);
        }

        return result;
    }



    @Override
    public synchronized boolean addCount(Long id) {
        SysFile sysFile = getById(id);
        if (ObjectUtil.isEmpty(sysFile)) {
            throw new AiurtBootException("未找到相关数据");
        }
        sysFile.setDownSize(sysFile.getDownSize() == null ? 1 : sysFile.getDownSize() + 1);
        boolean result = updateById(sysFile);
        return result;
    }

    @Override
    public SysFileInfo addDownload(SysFileInfo sysFileInfo) {
        return sysFileInfoService.addDownload(sysFileInfo);
    }

    /**
     * 处理文件名和类型
     *
     * @param sysFile      SysFile对象，保存的对象
     * @param sysFileParam sysFileParam对象，包含文件信息
     */
    private void processFileNameAndType(SysFile sysFile, SysFileParam sysFileParam) {
        if (StringUtils.isNotBlank(sysFileParam.getName())) {
            String name = sysFileParam.getName();
            String prefix = name.substring(0, name.indexOf(SymbolConstant.SPOT));
            sysFile.setName(StringUtils.isNotBlank(prefix) ? prefix : "未知文件名");

            String substring = name.substring(name.lastIndexOf(PatrolConstant.NO_SPL));
            if (StringUtils.isNotBlank(substring)) {
                String suffix = substring.replaceFirst(PatrolConstant.NO_SPL, "");
                if (StringUtils.isNotBlank(suffix)) {
                    sysFile.setType(suffix.toUpperCase());
                }
            } else {
                sysFile.setType("未知类型");
            }
        }
    }

    /**
     * 处理文件大小
     *
     * @param sysFile 保存SysFile对象
     * @param file    包含文件信息
     */
    private void processFileSize(SysFile sysFile, SysFileParam file) {
        if (StrUtil.isNotBlank(file.getFileSize())) {
            int fileSizeInBytes = Integer.parseInt(file.getFileSize());
            BigDecimal fileSizeInKB = NumberUtil.div(String.valueOf(fileSizeInBytes), String.valueOf(SysFileConstant.BYTES_IN_KB), 1);
            BigDecimal fileSizeInMB = NumberUtil.div(String.valueOf(fileSizeInBytes), String.valueOf(SysFileConstant.BYTES_IN_MB), 1);

            if (fileSizeInBytes >= 0 && fileSizeInBytes < SysFileConstant.BYTES_IN_KB) {
                sysFile.setFileSize(fileSizeInBytes + "B");
            } else if (fileSizeInBytes >= SysFileConstant.BYTES_IN_KB && fileSizeInBytes < SysFileConstant.BYTES_IN_MB) {
                sysFile.setFileSize(fileSizeInKB.stripTrailingZeros().toPlainString() + "KB");
            } else if (fileSizeInBytes >= SysFileConstant.BYTES_IN_MB) {
                sysFile.setFileSize(fileSizeInMB.stripTrailingZeros().toPlainString() + "MB");
            }
        }
    }

    /**
     * 保存SysFolderFilePermission
     *
     * @param sysFile SysFile对象，包含文件信息
     */
    private void saveSysFolderFilePermission(SysFile sysFile) {
        SysFolderFilePermission sysFolderFilePermission = new SysFolderFilePermission();
        sysFolderFilePermission.setFileId(sysFile.getId());

        // 继承文件夹的权限
        LambdaQueryWrapper<SysFolderFilePermission> lam = new LambdaQueryWrapper<>();
        lam.select(SysFolderFilePermission::getUserId);
        lam.select(SysFolderFilePermission::getOrgCode);
        lam.select(SysFolderFilePermission::getFolderId);
        lam.select(SysFolderFilePermission::getPermission);
        lam.eq(SysFolderFilePermission::getFolderId, sysFile.getTypeId());
        List<SysFolderFilePermission> sysFolderFilePermissions = sysFolderFilePermissionService.list(lam);
        sysFolderFilePermissionService.saveBatch(sysFolderFilePermissions, 500);
    }

    /**
     * 根据用户姓名模糊查询用户列表
     *
     * @param sysFileWebParam 文件查询参数对象，包含查询条件
     * @return 匹配的用户姓名列表
     */
    private List<String> getUserNames(SysFileWebParam sysFileWebParam) {
        List<String> userNames = new ArrayList<>();
        if (StringUtils.isNotBlank(sysFileWebParam.getCreateByName())) {
            userNames = sysBaseApi.getUserLikeName(sysFileWebParam.getCreateByName());
        }
        return userNames;
    }

    /**
     * 将文件信息保存到 Elasticsearch
     *
     * @param sysFile 文件信息对象
     * @return Elasticsearch 索引响应
     */
    private IndexResponse saveEsDate(SysFile sysFile) {
        IndexResponse response = null;
        try {
            // 根据文件地址获取文件
            byte[] bytes = this.getBytes(sysFile);

            FileDataDTO fileDataDTO = new FileDataDTO();
            fileDataDTO.setId(String.valueOf(sysFile.getId()));
            fileDataDTO.setName(sysFile.getName());
            fileDataDTO.setTypeId(String.valueOf(sysFile.getTypeId()));
            fileDataDTO.setFormat(sysFile.getType());
            fileDataDTO.setAddress(sysFile.getUrl());
            fileDataDTO.setFileBytes(bytes);

            response = esFileApi.saveFileData(fileDataDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }

    /**
     * 将 SysFile 对象转换为字节数组
     *
     * @param sysFile SysFile 对象
     * @return 转换后的字节数组
     * @throws IOException 如果转换过程中发生 I/O 异常
     */
    private byte[] getBytes(SysFile sysFile) throws IOException {
        byte[] bytes = null;
        String attName = null;
        String filePath = null;
        String url = sysFile.getUrl();
        filePath = StrUtil.subBefore(url, "?", false);

        filePath = filePath.replace("..", "").replace("../", "");
        if (filePath.endsWith(SymbolConstant.COMMA)) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }

        SysAttachment sysAttachment = sysBaseApi.getFilePath(filePath);
        InputStream inputStream = null;

        if (Objects.isNull(sysAttachment)) {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new AiurtBootException("文件不存在..");
            }
            if (StrUtil.isBlank(sysFile.getName())) {
                attName = file.getName();
            } else {
                attName = sysFile.getName();
            }
            inputStream = new BufferedInputStream(new FileInputStream(filePath));
            bytes = StreamUtils.copyToByteArray(inputStream);
            //关闭流
            inputStream.close();

        } else {
            if (StrUtil.equalsIgnoreCase("minio", sysAttachment.getType())) {
                inputStream = MinioUtil.getMinioFile("platform", sysAttachment.getFilePath());
            } else {
                String imgPath = uploadPath + File.separator + sysAttachment.getFilePath();
                File file = new File(imgPath);
                if (!file.exists()) {

                    throw new RuntimeException("文件[" + imgPath + "]不存在..");
                }
                inputStream = new BufferedInputStream(new FileInputStream(imgPath));
            }
            bytes = StreamUtils.copyToByteArray(inputStream);
            //关闭流
            inputStream.close();
        }
        return bytes;
    }

    /**
     * 检查用户是否有删除文件权限
     *
     * @param sysFile   文件对象
     * @param loginUser 登录用户对象
     * @throws AiurtBootException 如果用户没有权限删除文件，抛出异常
     */
    private void checkDeletePermission(SysFile sysFile, LoginUser loginUser) throws AiurtBootException {
        LambdaQueryWrapper<SysFolderFilePermission> lam = new LambdaQueryWrapper<>();
        lam.eq(SysFolderFilePermission::getFileId, sysFile.getId());
        lam.ge(SysFolderFilePermission::getPermission, SysFileConstant.PERMISSION_DELETE);
        lam.eq(SysFolderFilePermission::getDelFlag, CommonConstant.DEL_FLAG_0);
        lam.and(wrapper -> wrapper.eq(SysFolderFilePermission::getUserId, loginUser.getId())
                .or().eq(SysFolderFilePermission::getOrgCode, loginUser.getOrgCode()));
        lam.groupBy(SysFolderFilePermission::getFileId);

        long count = sysFolderFilePermissionService.count(lam);
        if (count <= 0) {
            throw new AiurtBootException("您没有权限删除此项数据");
        }
    }

    /**
     * 更新文件信息
     *
     * @param sysFile 文件类型对象
     * @param param   更新参数对象
     * @return true表示更新成功，false表示更新失败
     */
    public boolean updateFile(SysFile sysFile, SysFileParam param) {
        sysFile.setName(param.getName().trim());
        return this.updateById(sysFile);
    }

    /**
     * 删除原来的权限信息
     *
     * @param sysFile 文件对象
     */
    public boolean deleteOriginalPermissions(SysFile sysFile) {
        // 构造查询条件
        LambdaQueryWrapper<SysFolderFilePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SysFolderFilePermission::getId);
        lambdaQueryWrapper.eq(SysFolderFilePermission::getFileId, sysFile.getId());

        // 查询需要删除的权限信息
        List<SysFolderFilePermission> sysFolderFilePermissions = sysFolderFilePermissionService.list(lambdaQueryWrapper);

        // 提取权限ID列表
        List<String> permissionIds = Optional.ofNullable(sysFolderFilePermissions)
                .orElse(Collections.emptyList())
                .stream()
                .map(SysFolderFilePermission::getId)
                .collect(Collectors.toList());

        // 批量删除权限信息
        return sysFolderFilePermissionService.removeBatchByIds(permissionIds, 500);
    }
}
