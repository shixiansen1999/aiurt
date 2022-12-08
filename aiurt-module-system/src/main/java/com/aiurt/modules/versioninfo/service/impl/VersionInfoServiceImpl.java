package com.aiurt.modules.versioninfo.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.versioninfo.entity.VersionInfo;
import com.aiurt.modules.versioninfo.mapper.VersionInfoMapper;
import com.aiurt.modules.versioninfo.service.IVersionInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: bd_version_info
 * @Author: jeecg-boot
 * @Date: 2021-05-10
 * @Version: V1.0
 */
@Service
public class VersionInfoServiceImpl extends ServiceImpl<VersionInfoMapper, VersionInfo> implements IVersionInfoService {
    /**
     * 添加app版本信息
     *
     * @param versionInfo
     */
    @Override
    public void insertAppInfo(VersionInfo versionInfo) {
        VersionInfo versionInfo1 = baseMapper.selectByVid(String.valueOf(versionInfo.getVersionId()));
        if (versionInfo1 != null && versionInfo1.getUpdateTime() != null) {
            String APP_INfO_ERROR = "上传失败,版本号已重复,当前服务器最新版本号为";
            throw new JeecgBootException(APP_INfO_ERROR + versionInfo1.getVersionId() + ",更新时间为" + versionInfo1.getUpdateTime().toString());
        } else {
            versionInfo.setUpdateTime(new Date());
            baseMapper.insert(versionInfo);
        }
    }

    /**
     * 查询最新App版本
     *
     * @return
     */
    @Override
    public VersionInfo selectLatest() {
        return baseMapper.selectLatest();
    }

    @Override
    public VersionInfo checkUpdateApp(String version) {
        LambdaQueryWrapper<VersionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VersionInfo::getVersionId, version).orderByDesc(VersionInfo::getCreateTime);
        List<VersionInfo> versionInfoList = baseMapper.selectList(queryWrapper);

        if (CollectionUtil.isEmpty(versionInfoList)) {
            return null;
        }

        VersionInfo versionInfo = null;
        try {
            Date createTime = versionInfoList.get(0).getCreateTime();
            LambdaQueryWrapper<VersionInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.gt(VersionInfo::getCreateTime, createTime).orderByDesc(VersionInfo::getCreateTime).last("limit 1");

            versionInfo = baseMapper.selectOne(wrapper);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return versionInfo;
    }
}
