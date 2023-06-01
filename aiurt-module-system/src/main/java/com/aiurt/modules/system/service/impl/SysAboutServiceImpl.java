package com.aiurt.modules.system.service.impl;


import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.git.config.GitProperties;
import com.aiurt.modules.git.entity.SysTemVersionInfo;
import com.aiurt.modules.system.entity.SysAbout;
import com.aiurt.modules.system.mapper.SysAboutMapper;
import com.aiurt.modules.system.service.ISysAboutService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 关于
 * @Author: swsc
 * @Date:   2021-12-01
 * @Version: V1.0
 */
@Slf4j
@Service
public class SysAboutServiceImpl extends ServiceImpl<SysAboutMapper, SysAbout> implements ISysAboutService {

    @Override
    public SysTemVersionInfo queryVersionInfo() {
        SysTemVersionInfo versionInfo = new SysTemVersionInfo();
        try {
            LambdaQueryWrapper<SysAbout> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysAbout::getDelFlag, CommonConstant.DEL_FLAG_0).orderByDesc(SysAbout::getCreateTime).last("limit 1");
            String buildTime = GitProperties.init("git.build.time");
            String commitId = GitProperties.init("git.commit.id.abbrev");
            versionInfo = SysTemVersionInfo.builder()
                    .buildTime(buildTime)
                    .projectVersion("3.2.0")
                    .gitCommitId(commitId).build();
            SysAbout sysAbout = this.getOne(wrapper);
            if (Objects.nonNull(sysAbout)) {
                versionInfo.setProjectVersion(sysAbout.getVersion());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return versionInfo;
    }
}
