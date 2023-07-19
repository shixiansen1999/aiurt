package com.aiurt.modules.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.dto.DeviceDTO;
import com.aiurt.modules.common.entity.SelectTable;
import com.aiurt.modules.common.service.ICommonService;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.aiurt.modules.position.service.ICsLineService;
import com.aiurt.modules.position.service.ICsStationPositionService;
import com.aiurt.modules.position.service.ICsStationService;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.service.ICsUserStaionService;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserStationModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
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

    /**
     * 系统管理员角色编码
     */
    private static final String ADMIN = "admin";


    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ICsUserStaionService userStationService;

    @Autowired
    private ICsLineService lineService;

    @Autowired
    private ICsStationService stationService;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private ICsStationPositionService stationPositionService;


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
        List<DictModel> sysPost = sysBaseApi.getDictItems("sys_post");
        Map<String, String> sysPostMap = new HashMap<>(1);
        if (CollUtil.isNotEmpty(sysPost)) {
           sysPostMap = sysPost.stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (oldValue, newValue) -> newValue));
        }
        Map<String, String> roleNamesByUserIds = sysBaseApi.getRoleNamesByUserIds(null);
        dealUser(resultList, ignoreUserId,majorId, sysPostMap, roleNamesByUserIds);
        List<SelectTable> tableList = screenTree(resultList, keys);
        // 遍历所有部门，计算 subUserNum
        for (SelectTable table : tableList) {
            table.calculateSubUserNum();
        }
        return tableList;
//        return resultList;
    }


    @Override
    public List<SelectTable> queryDevice(DeviceDTO deviceDTO) {
        LambdaQueryWrapper<Device> queryWrapper = BuildDeviceQueryWrapper(deviceDTO);
        queryWrapper.eq(Device::getDelFlag, 0);
        List<Device> csMajorList = deviceService.getBaseMapper().selectList(queryWrapper);

        List<SelectTable> list = csMajorList.stream().map(device -> {
            SelectTable table = new SelectTable();
            table.setLabel(String.format("%s(%s)", device.getName(), device.getCode()));
            table.setValue(device.getCode());
            table.setDeviceTypeCode(device.getDeviceTypeCode());
            return table;
        }).collect(Collectors.toList());

        return list;
    }

    private void dealUser(List<SelectTable> children, String ignoreUserId,String majorId, Map<String, String> sysPostMap, Map<String, String> roleNamesByUserIds) {
        if (CollectionUtil.isEmpty(children)) {
            return;
        }
        for (SelectTable child : children) {
            List<SelectTable> list = child.getChildren();
            dealUser(list, ignoreUserId,majorId, sysPostMap, roleNamesByUserIds);
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
                table.setTitle(sysUser.getRealname());
                table.setOrgCode(child.getKey());
                table.setOrgName(child.getLabel());
                List<String> jobNames = StrUtil.splitTrim(sysUser.getJobName(), ",");
                if (CollUtil.isNotEmpty(jobNames)) {
                    String postName = jobNames.stream().map(e -> sysPostMap.get(e)).collect(Collectors.joining(","));
                    table.setPostName(postName);
                }
                table.setRoleName(roleNamesByUserIds.get(sysUser.getId()));
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

    /**
     * 异步加载树形结构
     *
     * @param name
     * @param pid
     * @return
     */
    @Override
    public List<SelectTable> queryPositionTreeAsync(String name, String pid, String queryAll) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        String roleCodes = loginUser.getRoleCodes();

        // 顶级位置数
        if (StrUtil.isBlank(pid) || StrUtil.equalsAnyIgnoreCase(pid, "0")) {
            List<CsUserStationModel> stationModelList = Collections.emptyList();
            // 根据个人管理的站点
            if ((StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) || StrUtil.equalsIgnoreCase(queryAll, String.valueOf(CommonConstant.DEL_FLAG_1))) {
                stationModelList = userStationService.queryAllStation(null);
            }else {
                stationModelList = sysBaseApi.getStationByUserId(userId);
            }

            Set<String> lineSet = stationModelList.stream().map(CsUserStationModel::getLineCode).collect(Collectors.toSet());
            if (CollUtil.isEmpty(lineSet)) {
                return Collections.emptyList();
            }

            LambdaQueryWrapper<CsLine> lineLambdaQueryWrapper = new LambdaQueryWrapper<CsLine>()
                    .in(CsLine::getLineCode, lineSet).eq(CsLine::getDelFlag, 0).orderByAsc(CsLine::getSort).orderByAsc(CsLine::getLineCode);

            List<CsLine> lineList = lineService.getBaseMapper().selectList(lineLambdaQueryWrapper);

            List<SelectTable> list = lineList.stream().map(csLine -> {
                SelectTable table = new SelectTable();
                table.setLabel(csLine.getLineName());
                table.setValue(csLine.getLineCode());
                table.setTitle(csLine.getLineName());
                table.setLevel(1);
                table.setLineCode(csLine.getLineCode());
                table.setPid("0");
                table.setId(csLine.getLineCode());
                table.setIsLeaf(false);
                return table;
            }).collect(Collectors.toList());


            return list;
        }

        List<CsUserStationModel> stationModelList = null;
        // 不等于0， 子节点,需要判断是站点还是位置。 判断是否有权限
        if ((StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) || StrUtil.equalsIgnoreCase(queryAll, String.valueOf(CommonConstant.DEL_FLAG_1))) {
            stationModelList = userStationService.queryAllStation(pid);
        }else {
            stationModelList = userStationService.queryByUserIdAndLineCode(userId, pid);
        }

        //  构造站点树
        if (CollUtil.isNotEmpty(stationModelList)) {
            List<SelectTable> list = stationModelList.stream().map(csUserStationModel -> {
                SelectTable selectTable = new SelectTable();
                selectTable.setValue(csUserStationModel.getStationCode());
                selectTable.setTitle(csUserStationModel.getStationName());
                selectTable.setLabel(csUserStationModel.getStationName());
                selectTable.setLevel(2);
                selectTable.setId(csUserStationModel.getStationCode());
                selectTable.setLineCode(csUserStationModel.getLineCode());
                selectTable.setStationCode(csUserStationModel.getStationCode());
                selectTable.setPid(pid);
                selectTable.setIsLeaf(false);
                return selectTable;
            }).collect(Collectors.toList());
            return list;
        }

        LambdaQueryWrapper<CsStationPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStationPosition::getStaionCode, pid).eq(CsStationPosition::getDelFlag, CommonConstant.DEL_FLAG_0);

        List<CsStationPosition> list = stationPositionService.list(wrapper);

        List<SelectTable> collect = list.stream().map(csStationPosition -> {
            SelectTable build = SelectTable.builder()
                    .value(csStationPosition.getPositionCode())
                    .title(csStationPosition.getPositionName())
                    .label(csStationPosition.getPositionName())
                    .level(3)
                    .id(csStationPosition.getPositionCode())
                    .pid(pid)
                    .isLeaf(true)
                    .lineCode(csStationPosition.getLineCode())
                    .stationCode(csStationPosition.getStaionCode())
                    .positionCode(csStationPosition.getPositionCode()).build();
            return build;
        }).collect(Collectors.toList());

        return collect;
    }

    /**
     * 分页查询设备
     *
     * @param deviceDTO
     * @return
     */
    @Override
    public IPage<Device> queryPageDevice(DeviceDTO deviceDTO) {
        LambdaQueryWrapper<Device> queryWrapper = BuildDeviceQueryWrapper(deviceDTO);
        queryWrapper.eq(Device::getDelFlag, 0);
        Page<Device> page = new Page<>(deviceDTO.getPageNo(), deviceDTO.getPageSize());
        IPage<Device> pageList = deviceService.page(page, queryWrapper);
        return pageList;
    }

    /**
     * 线路站点
     *
     * @param name
     * @param queryAll
     * @return
     */
    @Override
    public List<SelectTable> queryStationTree(String name, String queryAll) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = loginUser.getId();
        String roleCodes = loginUser.getRoleCodes();

        List<CsUserStationModel> stationModelList = Collections.emptyList();

        // 查看全部的站点， // 系统管理员不做权限过滤
        if ((StrUtil.isNotBlank(roleCodes) && roleCodes.indexOf(ADMIN)>-1) || StrUtil.equalsIgnoreCase(queryAll, String.valueOf(CommonConstant.DEL_FLAG_1))) {
            stationModelList = userStationService.queryAllStation(null);
        }else {
            // 根据个人管理的站点
            stationModelList = sysBaseApi.getStationByUserId(userId);
        }

        Map<String, List<CsUserStationModel>> stationMap = stationModelList.stream().collect(Collectors.groupingBy(CsUserStationModel::getLineCode,LinkedHashMap::new,Collectors.toList()));

        List<CsLine> lineList = lineService.getBaseMapper().selectList(new LambdaQueryWrapper<CsLine>().eq(CsLine::getDelFlag,0));

        Map<String, CsLine> lineMap = lineList.stream().collect(Collectors.toMap(CsLine::getLineCode, t->t, (t1,t2)->t2));

        List<SelectTable> list = new ArrayList<>();
        stationMap.keySet().stream().forEach(lineCode -> {
            CsLine csLine = lineMap.getOrDefault(lineCode, new CsLine());
            SelectTable table = new SelectTable();
            table.setLabel(csLine.getLineName());
            table.setValue(lineCode);
            table.setLevel(1);
            table.setKey(csLine.getId());
            table.setTitle(csLine.getLineName());
            table.setLineCode(lineCode);
            table.setIsLeaf(false);
            table.setPid("0");
            table.setId(lineCode);
            //
            List<CsUserStationModel> csStationList = stationMap.getOrDefault(lineCode, Collections.emptyList());
            List<SelectTable> lv2List = csStationList.stream().map(csStation -> {
                SelectTable selectTable = new SelectTable();
                selectTable.setValue(csStation.getStationCode());
                selectTable.setLabel(csStation.getStationName());
                selectTable.setTitle(csStation.getStationName());
                selectTable.setLevel(2);
                selectTable.setKey(csStation.getStationId());
                selectTable.setLineCode(lineCode);
                selectTable.setStationCode(csStation.getStationCode());
                selectTable.setPid(lineCode);
                selectTable.setId(csStation.getStationCode());
                selectTable.setIsLeaf(true);
                return selectTable;
            }).collect(Collectors.toList());
            table.setChildren(lv2List);
            list.add(table);
        });
        //树形搜索匹配
        if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(list)) {
            sysBaseApi.processingTreeList(name,list);
        }
        return list;
    }

    @NotNull
    private LambdaQueryWrapper<Device> BuildDeviceQueryWrapper(DeviceDTO deviceDTO) {
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
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
            if (StrUtil.isNotBlank(deviceDTO.getCode())) {
                List<String> list = StrUtil.splitTrim(deviceDTO.getCode(), ',');
                if (CollUtil.isNotEmpty(list)) {
                    queryWrapper.in(Device::getCode, list);
                }
            }
        }
        return queryWrapper;
    }
}
