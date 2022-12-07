package com.aiurt.modules.versioninfo.service;

import com.aiurt.modules.versioninfo.entity.VersionInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: bd_version_info
 * @Author: jeecg-boot
 * @Date: 2021-05-10
 * @Version: V1.0
 */
public interface IVersionInfoService extends IService<VersionInfo> {
    //添加app版本信息
    void insertAppInfo(VersionInfo versionInfo);

    //查询最新App版本
    VersionInfo selectLatest();

    VersionInfo checkUpdateApp(String version);
}
