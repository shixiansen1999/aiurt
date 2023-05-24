package com.aiurt.modules.sysfile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.sysfile.constant.PatrolConstant;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.mapper.SysFileManageMapper;
import com.aiurt.modules.sysfile.param.SysFileParam;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.service.ISysFileManageService;
import com.aiurt.modules.sysfile.service.ISysFolderFilePermissionService;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.vo.SysFileVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

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
    @Value("${jeecg.path.upload}")
    private String uploadPath;

    @Override
    public Page<SysFileManageVO> getFilePageList(Page<SysFileManageVO> page, SysFileWebParam sysFile) {
        return null;
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
                saveSysFolderFilePermission(sysFile, loginUser);

                result.success("添加成功！");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.error500("添加失败");
            }
        }
        return result;
    }


    @Override
    public int editFile(SysFileParam sysFileParam) {
        return 0;
    }

    @Override
    public int removeById(String id) {
        return 0;
    }

    @Override
    public SysFileVO getById(String id) {
        return null;
    }

    @Override
    public ModelAndView reportExportDownloadList(Long fileId) {
        return null;
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
     * @param sysFile   SysFile对象，包含文件信息
     * @param loginUser 登录用户对象
     */
    private void saveSysFolderFilePermission(SysFile sysFile, LoginUser loginUser) {
        SysFolderFilePermission sysFolderFilePermission = new SysFolderFilePermission();
        sysFolderFilePermission.setFileId(sysFile.getId());
        sysFolderFilePermission.setUserId(loginUser.getId());

        // 继承文件夹的权限

        sysFolderFilePermissionService.save(sysFolderFilePermission);
    }

}
