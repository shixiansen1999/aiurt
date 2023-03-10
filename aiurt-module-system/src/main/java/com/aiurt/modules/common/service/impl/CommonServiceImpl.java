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
    public List<SelectTable> queryDepartUserTree(List<String> orgIds, String ignoreUserId,String majorId,List<String> keys) {
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
        dealUser(resultList, ignoreUserId,majorId);
        List<SelectTable> tableList = screenTree(resultList, keys);

        return tableList;
//        return resultList;
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

    private void dealUser(List<SelectTable> children, String ignoreUserId,String majorId) {
        if (CollectionUtil.isEmpty(children)) {
            return;
        }
        for (SelectTable child : children) {
            List<SelectTable> list = child.getChildren();
            dealUser(list, ignoreUserId,majorId);
            if (CollectionUtil.isEmpty(list)) {
                list = new ArrayList<>();
            }
            // 部门id
            String orgId = child.getValue();
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getOrgId, orgId);
            wrapper.eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0);
            wrapper.eq(SysUser::getStatus, CommonConstant.USER_UNFREEZE);
            wrapper.apply(StrUtil.isNotBlank(majorId),
                    "id in (select user_id from cs_user_major where 1=1 and major_id in (select id from cs_major where 1=1 and ( id = {0} or major_code = {0})))",
                    majorId);
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

    private List<SelectTable> screenTree(List<SelectTable> tableList,List<String> keys){
        List<SelectTable> list = new ArrayList<>();
        if (listIsNotEmpty(tableList)&&keys!=null){
            for (SelectTable table:tableList){
                List<SelectTable> tableChildren = table.getChildren();
                //递归筛选完成后的返回的需要添加的数据
                SelectTable fiterTree = getFiterTree(table,tableChildren,keys);
                if (isNotEmpty(fiterTree)){
                    list.add(fiterTree);
                }
            }
        }else {
            return tableList;
        }
        return list;
    }

    public static SelectTable getFiterTree(SelectTable table,List<SelectTable> tableChildren,List<String> keys){
        //作为筛选条件的判断值
        String key = table.getKey();
        //有子集时继续向下寻找
        if (listIsNotEmpty(tableChildren)){
            List<SelectTable> addTable = new ArrayList<>();
            for (SelectTable newTable:tableChildren){
                List<SelectTable> children = newTable.getChildren();
                SelectTable fiterTree = getFiterTree(newTable, children, keys);

                //当子集筛选完不为空时添加
                if (isNotEmpty(fiterTree)){
                    addTable.add(fiterTree);
                }
            }
            //子集满足条件筛选时集合不为空时，替换对象集合内容并返回当前对象
            if (listIsNotEmpty(addTable)) {
                table.setChildren(addTable);
                return table;
                //当前对象子集对象不满足条件时，判断当前对象自己是否满足筛选条件，满足设置子集集合为空，并返回当前对象
            }else if (listIsEmpty(addTable)&& keys.contains(key)){
                table.setChildren(null);
                return table;
            }else {
                return null;
            }
        }else {
            if (keys.contains(key)){
                return table;
            }else {
                return null;
            }
        }

    }

    public static boolean listIsEmpty(Collection list){
        return  (null == list || list.size() == 0);
    }

    /**
     * 判断集合非空
     * @param list 需要判断的集合
     * @return 集合非空时返回 true
     */
    public static boolean listIsNotEmpty(Collection list){
        return !listIsEmpty(list);
    }

    /**
     * 判断对象为null或空时
     * @param object 对象
     * @return 对象为空或null时返回 true
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return (true);
        }
        if ("".equals(object)) {
            return (true);
        }
        if ("null".equals(object)) {
            return (true);
        }
        return (false);
    }

    /**
     * 判断对象非空
     * @param object 对象
     * @return 对象为非空时返回 true
     */
    public static boolean isNotEmpty(Object object) {
        if (object != null && !object.equals("") && !object.equals("null")) {
            return (true);
        }
        return (false);
    }
}
