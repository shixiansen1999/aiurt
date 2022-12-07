package com.aiurt.modules.versioninfo.mapper;

import com.aiurt.modules.versioninfo.entity.VersionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: bd_version_info
 * @Author: jeecg-boot
 * @Date:   2021-05-10
 * @Version: V1.0
 */
public interface VersionInfoMapper extends BaseMapper<VersionInfo> {
    //根据版本id查询版本信息
    VersionInfo selectByVid(String versionId);
    //查询最新App版本
    VersionInfo selectLatest();
}
