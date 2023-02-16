package com.aiurt.modules.usageconfig.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.aiurt.modules.usageconfig.service.UsageConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 系统配置
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Api(tags = "系统配置")
@RestController
@RequestMapping("/usageconfig")
@Slf4j
public class UsageConfigController extends BaseController<UsageConfig, UsageConfigService> {
    @Autowired
    private UsageConfigService usageConfigService;
    /**
     * 添加
     *
     * @param usageConfig
     * @return
     */
    @AutoLog(value = "系统配置-添加")
    @ApiOperation(value = "系统配置-添加", notes = "系统配置-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody UsageConfig usageConfig) {
        usageConfigService.save(usageConfig);
        return Result.OK("添加成功！");
    }
}
