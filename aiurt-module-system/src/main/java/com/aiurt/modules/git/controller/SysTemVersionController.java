package com.aiurt.modules.git.controller;

import com.aiurt.modules.git.config.GitProperties;
import com.aiurt.modules.git.entity.SysTemVersionInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fgw
 */
@Api(tags = "系统管理-系统版本")
@RestController
@RequestMapping("/sys")
@Slf4j
public class SysTemVersionController {

    /**
     * 查询系统版本记录
     *
     * @return
     */
    @ApiOperation(value = "查询系统版本记录", notes = "查询系统版本记录")
    @GetMapping(value = "/queryVersionInfo")
    public Result<?> queryVersionInfo() {
        String buildTime = GitProperties.init("git.build.time");
        String commitId = GitProperties.init("git.commit.id.abbrev");
        String version = GitProperties.getVersion();
        SysTemVersionInfo versionInfo = SysTemVersionInfo.builder()
                .buildTime(buildTime)
                .projectVersion("3.2.0")
                .gitCommitId(commitId).build();
        return Result.OK(versionInfo);
    }
}
