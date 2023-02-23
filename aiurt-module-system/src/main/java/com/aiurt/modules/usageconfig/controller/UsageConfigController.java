package com.aiurt.modules.usageconfig.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.base.controller.BaseController;
import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.dto.UsageConfigParamDTO;
import com.aiurt.modules.usageconfig.dto.UsageStatDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.aiurt.modules.usageconfig.service.UsageConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
     * 分页列表查询
     *
     * @param usageConfigDTO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "系统使用分类-分页列表查询")
    @ApiOperation(value = "系统使用分类-分页列表查询", notes = "系统使用分类-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UsageConfigDTO>> queryPageList(UsageConfigDTO usageConfigDTO,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Page<UsageConfigDTO> pageList = new Page<>(pageNo, pageSize);
        Page<UsageConfigDTO> list = usageConfigService.pageList(pageList, usageConfigDTO);
        return Result.OK(list);
    }

    /**
     * 系统使用分类-左侧树
     *
     * @param name
     * @return
     */
    @AutoLog(value = "系统使用分类-左侧树")
    @ApiOperation(value = "系统使用分类-左侧树", notes = "系统使用分类-左侧树")
    @PostMapping(value = "/tree")
    public Result<List<UsageConfigDTO>> tree(String name) {
        List<UsageConfigDTO> tree = usageConfigService.tree(name);
        return Result.OK(tree);
    }

    /**
     * 添加
     *
     * @param usageConfig
     * @return
     */
    @AutoLog(value = "系统使用分类-添加")
    @ApiOperation(value = "系统使用分类-添加", notes = "系统使用分类-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody UsageConfig usageConfig) {
        usageConfigService.save(usageConfig);
        if (!"0".equals(usageConfig.getPid())) {
            UsageConfig config = usageConfigService.getById((usageConfig.getPid()));
            config.setHasChild(1);
            usageConfigService.updateById(config);
        }
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param usageConfig
     * @return
     */
    @AutoLog(value = "系统使用分类-编辑")
    @ApiOperation(value = "系统使用分类-编辑", notes = "系统使用分类-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody UsageConfig usageConfig) {
        usageConfigService.updateById(usageConfig);
        return Result.OK("编辑成功！");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "系统使用分类-删除")
    @ApiOperation(value = "系统使用分类-删除", notes = "系统使用分类-删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        usageConfigService.removeById(id);
        return Result.OK("删除成功！");
    }


    @AutoLog(value = "系统使用-统计")
    @ApiOperation(value = "系统使用-统计", notes = "系统使用分类-统计")
    @GetMapping(value = "/getBusinessDataStatistics")
    public Result<IPage<UsageStatDTO>> getBusinessDataStatistics(UsageConfigParamDTO usageConfigParamDTO){
        IPage<UsageStatDTO> page = usageConfigService.getBusinessDataStatistics(usageConfigParamDTO);
        return Result.OK(page);
    }

    /**
     * 导出
     * @param request
     * @param usageConfig
     * @return
     */
    @AutoLog(value = "系统使用-统计导出")
    @ApiOperation(value = "系统使用-统计导出", notes = "系统使用-统计导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request,UsageConfig usageConfig) {
        return usageConfigService.exportXls(request, usageConfig);
    }
}
