package com.aiurt.modules.recycle.controller;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.recycle.constant.SysRecycleConstant;
import com.aiurt.modules.recycle.entity.SysRecycle;
import com.aiurt.modules.recycle.service.ISysRecycleService;
import com.aiurt.modules.system.service.ISysPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api(tags="回收站")
@RestController
@RequestMapping("/recycle")
@Slf4j
public class SysRecycleController {
    @Autowired
    private ISysRecycleService sysRecycleService;
    @Autowired
    private ISysPermissionService sysPermissionService;

    /**
     * 分页列表查询
     *
     * @param sysRecycle
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "回收站分页列表查询")
    @ApiOperation(value="回收站分页列表查询", notes="回收站分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysRecycle>> queryPageList(SysRecycle sysRecycle,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<SysRecycle> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<SysRecycle> queryWrapper = new LambdaQueryWrapper<>();

        String MODULE_NAME = "moduleName";
        String SUBMENU_NAME = "submenuName";
        String MODULE_URL = "moduleUrl";
        String SUBMENU_URL = "submenuUrl";

        // 根据模块名称查询时：
        String moduleName = sysRecycle.getModuleName();
        if (StrUtil.isNotBlank(moduleName)){
            // 获取模块名称及其子菜单的url
            List<Map<String, String>> moduleUrlAndSubmenuUrl = sysPermissionService.getUrlByModuleName(moduleName);
            if (moduleUrlAndSubmenuUrl.size() == 0) {
                return Result.ok(page);
            }
            List<String> urlList = new ArrayList<>();
            moduleUrlAndSubmenuUrl.forEach(map->{
                urlList.add(map.get(MODULE_URL));
                urlList.add(map.get(SUBMENU_URL));
            });
            queryWrapper.in(SysRecycle::getModuleUrl, urlList);
            queryWrapper.orderByDesc(SysRecycle::getCreateTime);
            IPage<SysRecycle> pageList = sysRecycleService.page(page, queryWrapper);
            List<SysRecycle> sysRecycleList = pageList.getRecords().stream().peek(recycle -> {
                for (Map<String, String> map : moduleUrlAndSubmenuUrl) {
                    if (recycle.getModuleUrl().equals(map.get(MODULE_URL)) || recycle.getModuleUrl().equals(map.get(SUBMENU_URL))) {
                        recycle.setModuleName(map.get(MODULE_NAME));
                        recycle.setSubmenu(map.get(SUBMENU_NAME));
                        break;
                    }
                }
            }).collect(Collectors.toList());
            pageList.setRecords(sysRecycleList);
            return Result.OK(pageList);
        } else {
            queryWrapper.eq(SysRecycle::getState, SysRecycleConstant.STATE_NORMAL);
            queryWrapper.isNotNull(SysRecycle::getModuleUrl);
            queryWrapper.orderByDesc(SysRecycle::getCreateTime);
            IPage<SysRecycle> pageList = sysRecycleService.page(page, queryWrapper);
            List<SysRecycle> sysRecycleList = pageList.getRecords().stream().peek(recycle -> {
                Map<String, String> moduleNameAndSubmenuName = sysPermissionService.getModuleNameAndSubmenuName(recycle.getModuleUrl());
                recycle.setModuleName(moduleNameAndSubmenuName.get(MODULE_NAME));
                recycle.setSubmenu(moduleNameAndSubmenuName.get(SUBMENU_NAME));
            }).collect(Collectors.toList());
            pageList.setRecords(sysRecycleList);
            return Result.OK(pageList);
        }
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "查询",operateType = 1,operateTypeAlias = "通过id查询回收站记录")
    @ApiOperation(value="通过id查询回收站记录", notes="通过id查询回收站记录")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        SysRecycle sysRecycle = sysRecycleService.getById(id);
        if(sysRecycle==null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sysRecycle);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "删除", operateType = 4, operateTypeAlias = "通过id删除回收站记录")
    @ApiOperation(value = "通过id删除回收站记录", notes = "通过id删除回收站记录")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        LambdaUpdateWrapper<SysRecycle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysRecycle::getState, SysRecycleConstant.STATE_DELETE);
        updateWrapper.eq(SysRecycle::getId, id);
        sysRecycleService.update(null, updateWrapper);
        return Result.OK("删除成功!");
    }

    /**
     * 通过ids批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "删除", operateType = 4, operateTypeAlias = "通过ids批量删除")
    @ApiOperation(value = "通过ids批量删除回收站记录", notes = "通过ids批量删除回收站记录")
    @DeleteMapping(value = "/deleteBatchByIds")
    public Result<?> deleteBatchByIds(@RequestParam(name = "ids", required = true) List<String> ids) {
        LambdaUpdateWrapper<SysRecycle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysRecycle::getState, SysRecycleConstant.STATE_DELETE);
        updateWrapper.in(SysRecycle::getId, ids);
        sysRecycleService.update(null, updateWrapper);
        return Result.OK("删除成功!");
    }

    /**
     * 通过id还原数据
     *
     * @param
     * @return
     */
    @AutoLog(value = "还原数据",operateType = 1,operateTypeAlias = "通过id还原数据")
    @ApiOperation(value="通过id还原数据", notes="通过id还原数据")
    @PostMapping(value = "/restoreById")
    public Result<?> restoreById(@RequestBody Map map) throws SQLException {
        return sysRecycleService.restoreById((String) map.get("id"));
    }

    /**
     * 通过ids批量还原数据
     *
     * @param
     * @return
     */
    @AutoLog(value = "还原数据",operateType = 1,operateTypeAlias = "通过ids批量还原数据")
    @ApiOperation(value="通过ids批量还原数据", notes="通过ids批量还原数据")
    @PostMapping(value = "/restoreBatchByIds")
    public Result<?> restoreBatchByIds(@RequestBody Map map) throws SQLException {
        List<String> ids = Arrays.asList(((String) map.get("ids")).split(","));
        return sysRecycleService.restoreBatchByIds(ids);
    }


}
