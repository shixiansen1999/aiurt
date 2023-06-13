package com.aiurt.modules.training.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.training.entity.TrainingPlanFile;
import com.aiurt.modules.training.mapper.TrainingPlanFileMapper;
import com.aiurt.modules.training.service.ITrainingPlanFileService;
import com.aiurt.modules.training.vo.TrainingPlanFileVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: TrainingPlanFileServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/28 16:49
 */

@Service
public class TrainingPlanFileServiceImpl extends ServiceImpl<TrainingPlanFileMapper, TrainingPlanFile> implements ITrainingPlanFileService {
    @Resource
    private ISysFolderService sysFolderService;
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public IPage<TrainingPlanFileVO> listByPlanId(Page<TrainingPlanFileVO> page, Long planId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        IPage<TrainingPlanFileVO> pageList = this.baseMapper.listByPlanId(page, planId);
        pageList.getRecords().forEach(p -> {
            if (ObjectUtil.isNotEmpty(p.getFileId())) {
                SysFileWebParam sysFileWebParam = new SysFileWebParam();
                sysFileWebParam.setId(String.valueOf(p.getFileId()));
                List<SysFileManageVO> result = this.baseMapper.getFilePageList(null, sysFileWebParam, loginUser.getId(), loginUser.getOrgCode(), null);
                if (CollUtil.isNotEmpty(result)) {
                    sysFileWebParam.setId(String.valueOf(p.getFileId()));
                    //用id查询，是只有一个的
                    SysFileManageVO sysFileManageVO = result.get(0);
                    p.setPermission(sysFileManageVO.getPermission());
                }
            }
        });
        return pageList;
    }

    @Override
    public Page<SysFileManageVO> getFilePageList(Page<SysFileManageVO> page, SysFileWebParam sysFileWebParam) {
        LoginUser loginUser = getLoginUser();

        trimSysFileWebParamName(sysFileWebParam);

        List<String> userNames = getUserNames(sysFileWebParam);

        // 为了查询文件夹时把他子级所有文件夹的文件也查出来，所以利用folderCodeCc文件夹编码右模糊匹配
        setFolderCode(sysFileWebParam);

        if (ObjectUtil.isNotEmpty(sysFileWebParam) && StrUtil.isNotEmpty(sysFileWebParam.getName())) {
            sysFileWebParam.setName(sysFileWebParam.getName().trim());
        }
        if (ObjectUtil.isNotEmpty(sysFileWebParam.getId())) {
            //判断是否是多个,因为前端组件问题，只用id传参并多个，要兼容其他功能调接口用id查询
            List<String> ids = StrUtil.splitTrim(sysFileWebParam.getId(), ",");
            if (ids.size() > 1) {
                sysFileWebParam.setSelections(ids);
            }else {
                sysFileWebParam.setId(ids.get(0));
            }
        }
        List<SysFileManageVO> result = this.baseMapper.getFilePageList(page, sysFileWebParam, loginUser.getId(), loginUser.getOrgCode(), userNames);

        return page.setRecords(result);
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

    /**
     * 如果 SysFileWebParam 对象不为空，并且名称字段（name）不为空字符串，则修剪 SysFileWebParam 对象的名称字段。
     *
     * @param sysFileWebParam 要修剪名称字段的 SysFileWebParam 对象。
     */
    private void trimSysFileWebParamName(SysFileWebParam sysFileWebParam) {
        if (ObjectUtil.isNotEmpty(sysFileWebParam)) {
            if (StrUtil.isNotEmpty(sysFileWebParam.getName())) {
                sysFileWebParam.setName(sysFileWebParam.getName().trim());
            }

            if (StrUtil.isNotEmpty(sysFileWebParam.getCreateByName())) {
                sysFileWebParam.setCreateByName(sysFileWebParam.getCreateByName().trim());
            }
        }
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
     * 设置文件夹编码到SysFileWebParam对象中
     * 根据传入的类型ID获取对应的文件夹编码，并将其设置到SysFileWebParam对象中
     *
     * @param sysFileWebParam SysFileWebParam对象，用于设置文件夹编码
     */
    private void setFolderCode(SysFileWebParam sysFileWebParam) {
        if (sysFileWebParam.getTypeId() != null) {
            SysFileType sysFileType = sysFolderService.getById(sysFileWebParam.getTypeId());
            if (ObjectUtil.isNotEmpty(sysFileType)) {
                sysFileWebParam.setFolderCodeCc(sysFileType.getFolderCodeCc());
            }
        }
    }
}
