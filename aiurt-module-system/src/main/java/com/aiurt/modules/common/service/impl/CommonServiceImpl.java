package com.aiurt.modules.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.common.service.ICommonService;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
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

    @Autowired
    private IDeviceService deviceService;

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

    @Override
    public List<SelectTable> queryDevice(DeviceDTO deviceDTO) {
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        //todo 查询当前人员所管辖的站所
        if (ObjectUtil.isNotEmpty(deviceDTO)) {
            if (StrUtil.isNotBlank(deviceDTO.getLineCode())) {
                queryWrapper.eq(Device::getLineCode, deviceDTO.getLineCode());
            }

            if (StrUtil.isNotBlank(deviceDTO.getDeviceTypeCode())) {
                queryWrapper.eq(Device::getDeviceTypeCode, deviceDTO.getDeviceTypeCode());
            }

            if (StrUtil.isNotBlank(deviceDTO.getMajorCode())) {
                queryWrapper.eq(Device::getMajorCode, deviceDTO.getMajorCode());
            }

            if (StrUtil.isNotBlank(deviceDTO.getSystemCode())) {
                queryWrapper.eq(Device::getSystemCode, deviceDTO.getSystemCode());
            }

            if (StrUtil.isNotBlank(deviceDTO.getStationCode())) {
                queryWrapper.eq(Device::getStationCode, deviceDTO.getStationCode());
            }

            if (StrUtil.isNotBlank(deviceDTO.getPositionCode())) {
                queryWrapper.eq(Device::getPositionCode, deviceDTO.getPositionCode());
            }

            if (StrUtil.isNotBlank(deviceDTO.getName())) {
                queryWrapper.like(Device::getName, deviceDTO.getName());
            }
            if (CollectionUtil.isNotEmpty(deviceDTO.getDeviceCodes())) {
                queryWrapper.in(Device::getCode, deviceDTO.getDeviceCodes());
            }
        }
        queryWrapper.eq(Device::getDelFlag, 0);
        List<Device> csMajorList = deviceService.getBaseMapper().selectList(queryWrapper);

        List<SelectTable> list = csMajorList.stream().map(device -> {
            SelectTable table = new SelectTable();
            table.setLabel(String.format("%s(%s)", device.getName(), device.getCode()));
            table.setValue(device.getCode());
            return table;
        }).collect(Collectors.toList());

        return list;
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
