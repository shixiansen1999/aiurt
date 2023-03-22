package com.aiurt.modules.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CacheConstant;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.modules.system.dto.SysPermissionDTO;
import com.aiurt.modules.system.entity.SysPermission;
import com.aiurt.modules.system.entity.SysPermissionDataRule;
import com.aiurt.modules.system.mapper.SysDepartPermissionMapper;
import com.aiurt.modules.system.mapper.SysDepartRolePermissionMapper;
import com.aiurt.modules.system.mapper.SysPermissionMapper;
import com.aiurt.modules.system.mapper.SysRolePermissionMapper;
import com.aiurt.modules.system.model.SysPermissionTree;
import com.aiurt.modules.system.model.TreeModel;
import com.aiurt.modules.system.service.ISysPermissionDataRuleService;
import com.aiurt.modules.system.service.ISysPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private ISysPermissionDataRuleService permissionDataRuleService;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Resource
    private SysDepartPermissionMapper sysDepartPermissionMapper;

    @Resource
    private SysDepartRolePermissionMapper sysDepartRolePermissionMapper;

    @Override
    public List<TreeModel> queryListByParentId(String parentId) {
        return sysPermissionMapper.queryListByParentId(parentId);
    }

    /**
     * 真实删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void deletePermission(String id) throws AiurtBootException {
        SysPermission sysPermission = this.getById(id);
        if (sysPermission == null) {
            throw new AiurtBootException("未找到菜单信息");
        }
        String pid = sysPermission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            Long count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
            if (count == 1) {
                //若父节点无其他子节点，则该父节点是叶子节点
                this.sysPermissionMapper.setMenuLeaf(pid, 1);
            }
        }
        sysPermissionMapper.deleteById(id);
        // 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
        this.removeChildrenBy(sysPermission.getId());
        //关联删除
        Map map = new HashMap(5);
        map.put("permission_id", id);
        //删除数据规则
        this.deletePermRuleByPermId(id);
        //删除角色授权表
        sysRolePermissionMapper.deleteByMap(map);
        //删除部门权限表
        sysDepartPermissionMapper.deleteByMap(map);
        //删除部门角色授权
        sysDepartRolePermissionMapper.deleteByMap(map);
    }

    /**
     * 根据父id删除其关联的子节点数据
     *
     * @return
     */
    public void removeChildrenBy(String parentId) {
        LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
        // 封装查询条件parentId为主键,
        query.eq(SysPermission::getParentId, parentId);
        // 查出该主键下的所有子级
        List<SysPermission> permissionList = this.list(query);
        if (permissionList != null && permissionList.size() > 0) {
            // id
            String id = "";
            // 查出的子级数量
            Long num = Long.valueOf(0);
            // 如果查出的集合不为空, 则先删除所有
            this.remove(query);
            // 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
            for (int i = 0, len = permissionList.size(); i < len; i++) {
                id = permissionList.get(i).getId();
                Map map = new HashMap(5);
                map.put("permission_id", id);
                //删除数据规则
                this.deletePermRuleByPermId(id);
                //删除角色授权表
                sysRolePermissionMapper.deleteByMap(map);
                //删除部门权限表
                sysDepartPermissionMapper.deleteByMap(map);
                //删除部门角色授权
                sysDepartRolePermissionMapper.deleteByMap(map);
                num = this.count(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
                // 如果有, 则递归
                if (num > 0) {
                    this.removeChildrenBy(id);
                }
            }
        }
    }

    /**
     * 逻辑删除
     */
    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void deletePermissionLogical(String id) throws AiurtBootException {
        SysPermission sysPermission = this.getById(id);
        if (sysPermission == null) {
            throw new AiurtBootException("未找到菜单信息");
        }
        String pid = sysPermission.getParentId();
        Long count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
        if (count == 1) {
            //若父节点无其他子节点，则该父节点是叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 1);
        }
        sysPermission.setDelFlag(1);
        this.updateById(sysPermission);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void addPermission(SysPermission sysPermission) throws AiurtBootException {
        //----------------------------------------------------------------------
        //判断是否是一级菜单，是的话清空父菜单
        if (CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
            sysPermission.setParentId(null);
        }
        //----------------------------------------------------------------------
        String pid = sysPermission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            //设置父节点不为叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 0);
        }
        sysPermission.setCreateTime(new Date());
        sysPermission.setDelFlag(0);
        sysPermission.setLeaf(true);
        this.save(sysPermission);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void editPermission(SysPermission sysPermission) throws AiurtBootException {
        SysPermission p = this.getById(sysPermission.getId());
        //TODO 该节点判断是否还有子节点
        if (p == null) {
            throw new AiurtBootException("未找到菜单信息");
        } else {
            sysPermission.setUpdateTime(new Date());
            //----------------------------------------------------------------------
            //Step1.判断是否是一级菜单，是的话清空父菜单ID
            if (CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
                sysPermission.setParentId("");
            }
            //Step2.判断菜单下级是否有菜单，无则设置为叶子节点
            Long count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, sysPermission.getId()));
            if (count == 0) {
                sysPermission.setLeaf(true);
            }
            //----------------------------------------------------------------------
            this.updateById(sysPermission);

            //如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
            String pid = sysPermission.getParentId();
            boolean f = (oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid) && oConvertUtils.isNotEmpty(p.getParentId());
            if (f) {
                //a.设置新的父菜单不为叶子节点
                this.sysPermissionMapper.setMenuLeaf(pid, 0);
                //b.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
                Long cc = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, p.getParentId()));
                if (cc == 0) {
                    if (oConvertUtils.isNotEmpty(p.getParentId())) {
                        this.sysPermissionMapper.setMenuLeaf(p.getParentId(), 1);
                    }
                }

            }
        }

    }

    @Override
    public List<SysPermission> queryByUser(String username, Integer isApp) {
        return this.sysPermissionMapper.queryByUser(username, isApp);
    }

    /**
     * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
     */
    @Override
    public void deletePermRuleByPermId(String id) {
        LambdaQueryWrapper<SysPermissionDataRule> query = new LambdaQueryWrapper<>();
        query.eq(SysPermissionDataRule::getPermissionId, id);
        Long countValue = this.permissionDataRuleService.count(query);
        if (countValue > 0) {
            this.permissionDataRuleService.remove(query);
        }
    }

    /**
     * 获取模糊匹配规则的数据权限URL
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE)
    public List<String> queryPermissionUrlWithStar() {
        return this.baseMapper.queryPermissionUrlWithStar();
    }

    @Override
    public boolean hasPermission(String username, SysPermission sysPermission) {
        int count = baseMapper.queryCountByUsername(username, sysPermission);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasPermission(String username, String url) {
        SysPermission sysPermission = new SysPermission();
        sysPermission.setUrl(url);
        int count = baseMapper.queryCountByUsername(username, sysPermission);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void batchStartDisable(List<SysPermissionDTO> condition) {
        if (CollUtil.isEmpty(condition)) {
            throw new AiurtBootException("参数为空");
        }
        List<SysPermission> arr = CollUtil.newArrayList();
        for (SysPermissionDTO sysPermissionDTO : condition) {
            if (ObjectUtil.isEmpty(sysPermissionDTO)) {
                continue;
            }
            SysPermission sysPermission = this.getById(sysPermissionDTO.getId());
            if (ObjectUtil.isEmpty(sysPermission)) {
                continue;
            }

            if (ObjectUtil.isNotEmpty(sysPermissionDTO.getRoute())) {
                sysPermission.setRoute(sysPermissionDTO.getRoute());
            }
            arr.add(sysPermission);
        }

        if (CollUtil.isEmpty(arr)) {
            throw new AiurtBootException("查询不到相关菜单数据");
        }

        this.updateBatchById(arr);
    }

    @Override
    public List<SysPermissionTree> getSystemSubmenuRecursive(String parentId) {
        List<SysPermissionTree> all = baseMapper.getAllSystemSubmenu();
        List<SysPermissionTree> treeChildIds = this.getChildrens(parentId, all);
        return treeChildIds;
    }

    /**
     * 递归查询所有子节点
     *
     * @param pidVal
     * @return
     */
    private List<SysPermissionTree> getChildrens(String pidVal, List<SysPermissionTree> all) {

        List<SysPermissionTree> children = all.stream().filter(treeEntity -> {
            return treeEntity.getParentId().equals(pidVal);
        }).map(treeEntity -> {
            //1、找到子菜单
            treeEntity.setChildren(ObjectUtil.isNotEmpty(getChildrens(treeEntity.getId(), all)) ? getChildrens(treeEntity.getId(), all) : new ArrayList<>());
            return treeEntity;
        }).collect(Collectors.toList());
        return children;
    }

    /**
     * 根据url查询模块名称和子菜单名称
     * @param url
     * @return
     */
    @Override
    public Map<String, String> getModuleNameAndSubmenuName(String url) {
        String MODULE_NAME = "moduleName";
        String SUBMENU_NAME = "submenuName";
        Map<String, String> moduleNameAndSubmenuNameMap = new HashMap<>();
        moduleNameAndSubmenuNameMap.put(MODULE_NAME, null);
        moduleNameAndSubmenuNameMap.put(SUBMENU_NAME, null);
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysPermission::getUrl, url);
        List<SysPermission> sysPermissions = sysPermissionMapper.selectList(queryWrapper);
        if (sysPermissions.size() == 0){
            return moduleNameAndSubmenuNameMap;
        }
        SysPermission sysPermission = sysPermissions.get(0);
        Integer menuType = sysPermission.getMenuType();
        if(menuType.equals(CommonConstant.MENU_TYPE_0)){
            moduleNameAndSubmenuNameMap.put(MODULE_NAME, sysPermission.getName());
        }else if(menuType.equals(CommonConstant.MENU_TYPE_1)){
            moduleNameAndSubmenuNameMap.put(SUBMENU_NAME, sysPermission.getName());
            SysPermission parent = this.getById(sysPermission.getParentId());
            moduleNameAndSubmenuNameMap.put(MODULE_NAME, parent.getName());
        } else if (menuType.equals(CommonConstant.MENU_TYPE_2)){
            SysPermission submenu = this.getById(sysPermission.getParentId());
            SysPermission parent = this.getById(submenu.getParentId());
            moduleNameAndSubmenuNameMap.put(MODULE_NAME, parent.getName());
            moduleNameAndSubmenuNameMap.put(SUBMENU_NAME, sysPermission.getName());
        }
        return moduleNameAndSubmenuNameMap;
    }

    /**
     * 根据模块名称，获取模块的url和其子菜单的url
     * @param moduleName
     * @return
     */
    @Override
    public List<Map<String, String>> getUrlByModuleName(String moduleName){
        String MODULE_NAME = "moduleName";
        String SUBMENU_NAME = "submenuName";
        String MODULE_URL = "moduleUrl";
        String SUBMENU_URL = "submenuUrl";
        List<Map<String, String>> moduleUrlAndSubMenuUrlList = new ArrayList<>();

        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SysPermission::getName, moduleName);
        queryWrapper.eq(SysPermission::getMenuType, CommonConstant.MENU_TYPE_0);
        queryWrapper.isNotNull(SysPermission::getUrl);
        // 查询模块
        List<SysPermission> moduleSysPermissionList = sysPermissionMapper.selectList(queryWrapper);
        for (SysPermission moduleSysPermission : moduleSysPermissionList) {
            LambdaQueryWrapper<SysPermission> submenuQueryWrapper = new LambdaQueryWrapper<>();
            submenuQueryWrapper.eq(SysPermission::getParentId, moduleSysPermission.getId());
            submenuQueryWrapper.eq(SysPermission::getMenuType, CommonConstant.MENU_TYPE_1);
            submenuQueryWrapper.isNotNull(SysPermission::getUrl);
            // 查询子菜单
            List<SysPermission> submenuSysPermissionList = sysPermissionMapper.selectList(submenuQueryWrapper);
            for (SysPermission submenuSysPermission : submenuSysPermissionList) {
                Map<String, String> moduleUrlAndSubMenuUrlMap = new HashMap<>();
                moduleUrlAndSubMenuUrlMap.put(MODULE_URL, moduleSysPermission.getUrl());
                moduleUrlAndSubMenuUrlMap.put(MODULE_NAME, moduleSysPermission.getName());
                moduleUrlAndSubMenuUrlMap.put(SUBMENU_NAME, submenuSysPermission.getName());
                moduleUrlAndSubMenuUrlMap.put(SUBMENU_URL, submenuSysPermission.getUrl());
                moduleUrlAndSubMenuUrlList.add(moduleUrlAndSubMenuUrlMap);
            }
        }
        return moduleUrlAndSubMenuUrlList;
    }
}
