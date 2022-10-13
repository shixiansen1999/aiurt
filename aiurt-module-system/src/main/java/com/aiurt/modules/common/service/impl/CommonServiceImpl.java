package com.aiurt.modules.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.common.service.ICommonService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 * @date 2022-09-19
 */
@Slf4j
@Service
public class CommonServiceImpl implements ICommonService {


    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    /**
     * 根据机构人员树
     *
     * @param orgIds       机构id
     * @param ignoreUserId 忽略的用户id
     * @return
     */
    @Override
    public List<SelectTable> queryDepartUserTree(List<String> orgIds, String ignoreUserId) {
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        if (CollectionUtil.isNotEmpty(orgIds)) {
            queryWrapper.in(SysDepart::getId, orgIds);
        }

        List<SysDepart> departList = sysDepartService.getBaseMapper().selectList(queryWrapper);
        List<SelectTable> treeList = departList.stream().map(entity -> {
            SelectTable table = new SelectTable();
            table.setValue(entity.getId());
            table.setLabel(entity.getDepartName());
            table.setIsOrg(true);
            table.setKey(entity.getOrgCode());
            table.setParentValue(StrUtil.isBlank(entity.getParentId()) ? "-9999" : entity.getParentId());
            return table;
        }).collect(Collectors.toList());

        Map<String, SelectTable> root = new LinkedHashMap<>();
        for (SelectTable item : treeList) {
            SelectTable parent = root.get(item.getParentValue());
            if (Objects.isNull(parent)) {
                parent = new SelectTable();
                root.put(item.getParentValue(), parent);
            }
            SelectTable table = root.get(item.getValue());
            if (Objects.nonNull(table)) {
                item.setChildren(table.getChildren());
            }
            root.put(item.getValue(), item);
            parent.addChildren(item);
        }
        List<SelectTable> resultList = new ArrayList<>();
        List<SelectTable> collect = root.values().stream().filter(entity -> StrUtil.isBlank(entity.getParentValue())).collect(Collectors.toList());
        for (SelectTable entity : collect) {
            resultList.addAll(CollectionUtil.isEmpty(entity.getChildren()) ? Collections.emptyList() : entity.getChildren());
        }
        dealUser(resultList, ignoreUserId);
        return resultList;
    }

    private void dealUser(List<SelectTable> children, String ignoreUserId) {
        if (CollectionUtil.isEmpty(children)) {
            return;
        }
        for (SelectTable child : children) {
            List<SelectTable> list = child.getChildren();
            dealUser(list, ignoreUserId);
            if (CollectionUtil.isEmpty(list)) {
                list = new ArrayList<>();
            }
            // 部门id
            String orgId = child.getValue();
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getOrgId, orgId);
            wrapper.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);
            wrapper.eq(SysUser::getStatus, CommonConstant.USER_UNFREEZE);
            if (StrUtil.isNotBlank(ignoreUserId)) {
                wrapper.notIn(SysUser::getId, Collections.singleton(ignoreUserId));
            }
            List<SysUser> sysUserList = sysUserService.getBaseMapper().selectList(wrapper);
            List<SelectTable> tableList = sysUserList.stream().map(sysUser -> {
                SelectTable table = new SelectTable();
                table.setKey(sysUser.getId());
                table.setValue(sysUser.getUsername());
                table.setLabel(sysUser.getRealname());
                table.setOrgCode(child.getKey());
                table.setOrgName(child.getLabel());
                return table;
            }).collect(Collectors.toList());
            child.setUserNum((long) tableList.size());
            list.addAll(list.size(), tableList);
            child.setChildren(list);
        }
    }
}
