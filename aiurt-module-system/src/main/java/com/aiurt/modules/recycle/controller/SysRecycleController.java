package com.aiurt.modules.recycle.controller;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.recycle.constant.SysRecycleConstant;
import com.aiurt.modules.recycle.entity.SysRecycle;
import com.aiurt.modules.recycle.service.ISysRecycleService;
import com.aiurt.modules.system.entity.SysPermission;
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
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;
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
        String moduleName = sysRecycle.getModuleName();
        Page<SysRecycle> page = new Page<>(pageNo, pageSize);

        String MODULE_NAME = "moduleName";
        String SUBMENU_NAME = "submenuName";

        LambdaQueryWrapper<SysPermission> sysPermissionQueryWrapper = new LambdaQueryWrapper<>();
//        sysPermissionQueryWrapper.like(StrUtil.isNotBlank(moduleName), SysPermission::getName, moduleName);
//        sysPermissionQueryWrapper.eq(StrUtil.isNotBlank(moduleName), SysPermission::getMenuType, CommonConstant.MENU_TYPE_0);
        sysPermissionQueryWrapper.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
        sysPermissionQueryWrapper.isNotNull(SysPermission::getUrl);
        List<SysPermission> sysPermissionList = sysPermissionService.list(sysPermissionQueryWrapper);
        if (sysPermissionList.size() == 0) {
            return Result.OK(page);
        }
//        List<String> urlList = new ArrayList<>();
        Map<String, SysPermission> SysPermissionMap = new HashMap<>();
        sysPermissionList.forEach(sysPermission -> {
            SysPermissionMap.put(sysPermission.getId(), sysPermission);
//            urlList.add(sysPermission.getUrl());
        });

        LambdaQueryWrapper<SysRecycle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRecycle::getState, SysRecycleConstant.STATE_NORMAL);
        queryWrapper.isNotNull(SysRecycle::getModuleUrl);
        queryWrapper.orderByDesc(SysRecycle::getCreateTime);
//        queryWrapper.in(StrUtil.isNotBlank(moduleName), SysRecycle::getModuleUrl, urlList);
//        IPage<SysRecycle> pageList = sysRecycleService.page(page, queryWrapper);
//        List<SysRecycle> sysRecycleList = pageList.getRecords().stream().peek(recycle -> {
//            Map<String, String> moduleNameAndSubmenuNameMap = getModuleNameAndSubmenuNameByUrl(SysPermissionMap, recycle.getModuleUrl());
//            recycle.setModuleName(moduleNameAndSubmenuNameMap.get(MODULE_NAME));
//            recycle.setSubmenu(moduleNameAndSubmenuNameMap.get(SUBMENU_NAME));
//        }).collect(Collectors.toList());
//        if (StrUtil.isNotBlank(moduleName)){
//            sysRecycleList = sysRecycleList.stream().filter(recycle -> recycle.getModuleName().contains(moduleName)).collect(Collectors.toList());
//        }
//        pageList.setRecords(sysRecycleList);
//        pageList.setTotal(sysRecycleList.size());
        List<SysRecycle> recycleList = sysRecycleService.list(queryWrapper);
        List<SysRecycle> sysRecycleList = recycleList.stream().peek(recycle -> {
            Map<String, String> moduleNameAndSubmenuNameMap = getModuleNameAndSubmenuNameByUrl(SysPermissionMap, recycle.getModuleUrl());
            recycle.setModuleName(moduleNameAndSubmenuNameMap.get(MODULE_NAME));
            recycle.setSubmenu(moduleNameAndSubmenuNameMap.get(SUBMENU_NAME));
        }).collect(Collectors.toList());
        // 模块名称过滤
        if (StrUtil.isNotBlank(moduleName)){
            sysRecycleList = sysRecycleList.stream().filter(recycle -> recycle.getModuleName().contains(moduleName)).collect(Collectors.toList());
        }
        page.setTotal(sysRecycleList.size());
        page.setRecords(sysRecycleList);
        return Result.OK(page);
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
    @AutoLog(value = "回收站-通过id删除", operateType = 4, operateTypeAlias = "通过id删除", permissionUrl = "/manage/CollectionList")
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
    @AutoLog(value = "回收站-批量删除", operateType = 4, operateTypeAlias = "批量删除", permissionUrl = "/manage/CollectionList")
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
    @ApiOperation(value="通过ids批量还原数据", notes="通过ids批量还原数据")
    @PostMapping(value = "/restoreBatchByIds")
    public Result<?> restoreBatchByIds(@RequestBody Map map) throws SQLException {
        List<String> ids = Arrays.asList(((String) map.get("ids")).split(","));
        return sysRecycleService.restoreBatchByIds(ids);
    }

    /**
     * 根据url获取模块名称和子菜单名称，要考虑到子菜单下还有可能有子菜单
     * @param map
     * @param url
     * @return
     */
    private Map<String, String> getModuleNameAndSubmenuNameByUrl(Map<String, SysPermission> map, String url){
        String matchId = null;
        String MODULE_NAME = "moduleName";
        String SUBMENU_NAME = "submenuName";

        Map<String, String> ModuleNameAndSubmenuNameMap = new HashMap<>();
        ModuleNameAndSubmenuNameMap.put(MODULE_NAME, null);
        ModuleNameAndSubmenuNameMap.put(SUBMENU_NAME, null);
        Set<String> keySet = map.keySet();
        // 匹配url所在的SysPermission，返回匹配的id
        for (String key : keySet) {
            if (map.get(key).getUrl().equals(url)) {
                matchId = map.get(key).getId();
                break;
            }
        }
        if (matchId == null){
            return ModuleNameAndSubmenuNameMap;
        }
        // 从匹配到的SysPermission往上找，直到找到根(模块)
        // 应该不会有10层，为了避免死循环，最多找10次
        for (int i = 0; i < 10; i++) {
            SysPermission matchSysPermission = map.get(matchId);
            if (matchSysPermission.getMenuType().equals(CommonConstant.MENU_TYPE_0)) {
                // 已经是根节点了，不用往上找了
                ModuleNameAndSubmenuNameMap.put(MODULE_NAME, matchSysPermission.getName());
                break;
            }else if (matchSysPermission.getMenuType().equals(CommonConstant.MENU_TYPE_1)){
                // 子菜单，继续往上找
                matchId = matchSysPermission.getParentId();
                if (ModuleNameAndSubmenuNameMap.get(SUBMENU_NAME) != null && matchSysPermission.getName() != null){
                    ModuleNameAndSubmenuNameMap.put(SUBMENU_NAME, matchSysPermission.getName() + "-" + ModuleNameAndSubmenuNameMap.get(SUBMENU_NAME));
                } else ModuleNameAndSubmenuNameMap.computeIfAbsent(SUBMENU_NAME, k -> matchSysPermission.getName());
            }else {
                // 按钮权限，继续往上找
                matchId = matchSysPermission.getParentId();
            }
        }
        return ModuleNameAndSubmenuNameMap;
    }

}
