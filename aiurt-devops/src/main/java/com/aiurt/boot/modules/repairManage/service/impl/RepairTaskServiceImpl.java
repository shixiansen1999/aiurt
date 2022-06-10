package com.aiurt.boot.modules.repairManage.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.constant.RepairContant;
import com.aiurt.boot.common.exception.SwscException;
import com.aiurt.boot.common.result.SpareResult;
import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.common.util.MyBeanUtils;
import com.aiurt.boot.modules.apphome.constant.UserTaskConstant;
import com.aiurt.boot.modules.apphome.service.UserTaskService;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.mapper.DeviceChangeSparePartMapper;
import com.aiurt.boot.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.repairManage.entity.RepairPool;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.aiurt.boot.modules.repairManage.entity.RepairTaskEnclosure;
import com.aiurt.boot.modules.repairManage.mapper.RepairPoolMapper;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskEnclosureMapper;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskMapper;
import com.aiurt.boot.modules.repairManage.service.IRepairTaskService;
import com.aiurt.boot.modules.repairManage.vo.DeviceQueryVO;
import com.aiurt.boot.modules.repairManage.vo.ReTaskDetailVO;
import com.aiurt.boot.modules.repairManage.vo.RepairPoolListVO;
import com.aiurt.boot.modules.repairManage.vo.RepairRecordVO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import com.aiurt.boot.modules.secondLevelWarehouse.service.IMaterialBaseService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartScrapService;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockService;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.mapper.SysDepartMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 检修单列表
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Service
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService {

    @Resource
    private RepairPoolMapper repairPoolMapper;

    @Resource
    private SysDepartMapper departMapper;

    @Resource
    private RepairTaskEnclosureMapper repairTaskEnclosureMapper;

    @Resource
    private IStationService stationService;

    @Resource
    private UserTaskService userTaskService;

    @Resource
    private DeviceChangeSparePartMapper deviceChangeSparePartMapper;

    @Resource
    private IDeviceChangeSparePartService deviceChangeSparePartService;

    @Resource
    private IMaterialBaseService materialBaseService;

    @Resource
    private ISparePartStockService partStockService;

    @Resource
    private ISparePartScrapService sparePartScrapService;


    @Override
    public Result confirmById(String id, Integer confirmStatus, String errorContent, String url) {
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null) {
            return Result.error("非法参数");
        }
        if (confirmStatus == 1) {
            repairTask.setStatus(RepairContant.REPAIRSTATUS[2]);
        } else {
            repairTask.setStatus(RepairContant.REPAIRSTATUS[3]);
            repairTask.setErrorContent(errorContent);
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        repairTask.setConfirmUserId(Optional.ofNullable(user).map(LoginUser::getId).orElse(""));
        repairTask.setConfirmUserName(Optional.ofNullable(user).map(LoginUser::getRealname).orElse("test"));
        repairTask.setConfirmTime(new Date());
        repairTask.setConfirmUrl(url);
        this.baseMapper.updateById(repairTask);
        return Result.ok();
    }

    @Override
    public Result receiptById(String id, Integer receiptStatus, String errorContent, String url) {
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null) {
            return Result.error("非法参数");
        }
        if (receiptStatus == 1) {
            repairTask.setStatus(RepairContant.REPAIRSTATUS[4]);
        } else {
            repairTask.setStatus(RepairContant.REPAIRSTATUS[5]);
            repairTask.setErrorContent(errorContent);
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        repairTask.setReceiptUserId(Optional.ofNullable(user).map(LoginUser::getId).orElse(""));
        repairTask.setReceiptUserName(Optional.ofNullable(user).map(LoginUser::getRealname).orElse("test"));
        repairTask.setReceiptTime(new Date());
        repairTask.setReceiptUrl(url);
        this.baseMapper.updateById(repairTask);
        return Result.ok();
    }

    @Override
    public Result<ReTaskDetailVO> queryDetailById(String id) {
        Result<ReTaskDetailVO> result = new Result<>();
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null) {
            return result.error500("非法参数");
        }

        String repairPoolIds = repairTask.getRepairPoolIds();
        //查询检修计划池内容
        ArrayList<RepairPoolListVO> list = new ArrayList<>();
        String[] split = repairPoolIds.split(",");
        for (String repariPoolId : split) {
            RepairPoolListVO repairPoolListVO = repairPoolMapper.selectTypeAndContentById(repariPoolId);
            if (repairPoolListVO == null) {
                return result.error500("该记录已被删除");
            }
            list.add(repairPoolListVO);
        }

        //如果有一条记录需要验收那么所有的任务需要验收
        List<RepairPool> repairPoolList = repairPoolMapper.selectList(new LambdaQueryWrapper<RepairPool>().in(RepairPool::getId, split));
        Integer isReceipt = 0;
        for (RepairPool pool : repairPoolList) {
            if (pool.getIsReceipt() == 1) {
                isReceipt = 1;
                break;
            }
        }
        RepairPool repairPool = repairPoolMapper.selectById(split[0]);

        List<DeviceChangeSparePart> parts = deviceChangeSparePartMapper.selectList(new LambdaQueryWrapper<DeviceChangeSparePart>()
                .eq(DeviceChangeSparePart::getType, 1)
                .eq(DeviceChangeSparePart::getDelFlag, CommonConstant.DEL_FLAG_0)
                .eq(DeviceChangeSparePart::getCode, id)
        );
        Set<String> codeSet = new HashSet<>();
        for (DeviceChangeSparePart part : parts) {
            String oldCode = part.getOldSparePartCode();
            String newCode = part.getNewSparePartCode();
            if (StringUtils.isNotBlank(oldCode)) {
                codeSet.add(oldCode);
            }
            if (StringUtils.isNotBlank(newCode)) {
                codeSet.add(newCode);
            }
        }



        //查询班组信息
        final Station station = stationService.getById(repairTask.getOrganizationId());
        SysDepart sysDepart = departMapper.selectById(station.getTeamId());
        ReTaskDetailVO vo = this.generateReTask(repairPool, repairTask, sysDepart.getDepartName(), list, isReceipt);

        if (CollectionUtils.isNotEmpty(codeSet)) {
            List<SpareResult> spareList = new ArrayList<>();
            List<MaterialBase> baseList = materialBaseService.lambdaQuery().eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0).in(MaterialBase::getCode, codeSet).list();
            Map<String, String> materialBaseMap = null;
            if (CollectionUtils.isNotEmpty(baseList)) {
                materialBaseMap = baseList.stream().collect(Collectors.toMap(MaterialBase::getCode, MaterialBase::getName));
                for (DeviceChangeSparePart part : parts) {
                    SpareResult sp = new SpareResult();
                    sp.setOldSparePartCode(part.getOldSparePartCode());
                    sp.setOldSparePartName(materialBaseMap.get(part.getOldSparePartCode()));
                    sp.setOldSparePartNum(part.getOldSparePartNum());
                    sp.setNewSparePartCode(part.getNewSparePartCode());
                    sp.setNewSparePartName(materialBaseMap.get(part.getNewSparePartCode()));
                    sp.setNewSparePartNum(part.getNewSparePartNum());
                    spareList.add(sp);
                }
                vo.setSpareResults(spareList);
            }
        }
        if (vo.getSpareResults()==null){
            vo.setSpareResults(new ArrayList<>());
        }
        if (station!=null) {
            vo.setStationCode(station.getStationCode());
            vo.setLineName(station.getLineName());
            vo.setStationName(station.getStationName());
        }
        result.setResult(vo);
        return result;
    }

    @Override
    public Result getDetailByUser(LoginUser user, String startTime, String endTime) {
        Result<List<ReTaskDetailVO>> result = new Result<>();
        final ArrayList<ReTaskDetailVO> reTaskDetailVOS = new ArrayList<>();
        final List<Station> stationList = stationService.list(new QueryWrapper<Station>().eq("team_id", user.getOrgId()).eq("del_flag", 0));
        final List<Integer> stationIdList = stationList.stream().map(Station::getId).collect(Collectors.toList());

        QueryWrapper<RepairTask> wrapper = new QueryWrapper<>();
        wrapper.in("organization_id", stationIdList)
                .apply(user.getId() != null, "find_in_set('" + user.getId() + "',staff_ids)")
                .ge("start_time", startTime.concat(" 00:00:00"))
                .le("end_time", endTime.concat(" 23:59:59"))
                .eq("del_flag", 0);
        final List<RepairTask> taskList = this.baseMapper.selectList(wrapper);
        if (taskList.size() == 0) {
            return result.success("本周无检修任务");
        }
        taskList.forEach(x -> {
            final Station station = stationService.getById(x.getOrganizationId());
            final String[] split = x.getRepairPoolIds().split(",");
            final QueryWrapper<RepairPool> repairPoolQueryWrapper = new QueryWrapper<>();
            repairPoolQueryWrapper.in("id", split);
            final List<RepairPool> repairPoolList = repairPoolMapper.selectList(repairPoolQueryWrapper);

            //如果有一条记录需要验收那么所有的任务需要验收
            Integer isReceipt = 0;
            for (RepairPool pool : repairPoolList) {
                if (pool.getIsReceipt() == 1) {
                    isReceipt = 1;
                    break;
                }
            }

            final List receiptList = MyBeanUtils.copyList(repairPoolList, RepairPoolListVO.class);
            ReTaskDetailVO vo = this.generateReTask(repairPoolList.get(0), x, user.getOrgName(), receiptList, isReceipt);
            vo.setStationCode(station.getStationCode());
            vo.setStationName(station.getStationName());
            vo.setLineName(station.getLineName());
            reTaskDetailVOS.add(vo);
        });

        result.setResult(reTaskDetailVOS);
        return result;
    }

//    @Override
//    public Result<ReTaskDetailVO> getDetailByUserOld(LoginUser user, String startTime, String endTime) {
//        Result<ReTaskDetailVO> result = new Result<>();
//        final List<Station> stationList = stationService.list(new QueryWrapper<Station>().eq("team_id", user.getOrgId()).eq("del_flag", 0));
//        final List<Integer> orgIds = stationList.stream().map(Station::getId).collect(Collectors.toList());
//        QueryWrapper<RepairPool> wrapper = new QueryWrapper<>();
//        wrapper.in("organization_id", orgIds)
//                .ge("start_time", startTime.concat(" 00:00:00"))
//                .le("end_time", endTime.concat(" 23:59:59"))
//                .eq("del_flag", 0);
//        List<RepairPool> repairPoolList = repairPoolMapper.selectList(wrapper);
//        if (repairPoolList.size() == 0) {
//            return result.success("本周无检修任务");
//        }
//        //如果有一条记录需要验收那么所有的任务需要验收
//        Integer isReceipt = 0;
//        for (RepairPool pool : repairPoolList) {
//            if (pool.getIsReceipt() == 1) {
//                isReceipt = 1;
//                break;
//            }
//        }
//        RepairPool repairPool = repairPoolList.get(0);
//        List list = MyBeanUtils.copyList(repairPoolList, RepairPoolListVO.class);
//        //未检修
//        if (repairPool.getStatus() == 0) {
//            ReTaskDetailVO vo = new ReTaskDetailVO();
//            vo.setWeeks(repairPool.getWeeks());
//            vo.setTeamName(user.getOrgName());
//            vo.setStatus(repairPool.getStatus());
//            vo.setStartTime(repairPool.getStartTime());
//            vo.setEndTime(repairPool.getEndTime());
//            vo.setRepairPoolList(list);
//            result.setResult(vo);
//            return result;
//        }
//        wrapper.apply(user.getId() != null, "find_in_set('" + user.getId() + "',repair_user_ids)");
//        final List<RepairPool> repairPools = repairPoolMapper.selectList(wrapper);
//        final List copyList = MyBeanUtils.copyList(repairPools, RepairPoolListVO.class);
//        List<Long> collect = repairPools.stream().map(RepairPool::getId).collect(Collectors.toList());
//        String repairPoolIds = StringUtils.join(collect, ",");
//        QueryWrapper<RepairTask> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("del_flag", 0).eq("repair_pool_ids", repairPoolIds).last("limit 1");
//        RepairTask repairTask = this.baseMapper.selectOne(queryWrapper);
//        if (repairTask == null) {
//            return result.error500("异常数据");
//        }
//        ReTaskDetailVO vo = this.generateReTask(repairPools.get(0), repairTask, user.getOrgName(), copyList, isReceipt);
//        result.setResult(vo);
//        return result;
//    }

//    @Override
//    public Result getDetailByUser(LoginUser user, String startTime, String endTime) {
//        Result<List<ReTaskDetailVO>> result = new Result<>();
//        final ArrayList<ReTaskDetailVO> reTaskDetailVOS = new ArrayList<>();
//        List<String> roleList = new ArrayList<String>();
//        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getId()));
//        if (userRole == null || userRole.size() <= 0) {
//            result.error500("未找到用户相关角色信息");
//        } else {
//            for (SysUserRole sysUserRole : userRole) {
//                final SysRole role = sysRoleService.getById(sysUserRole.getRoleId());
//                roleList.add(role.getRoleCode());
//            }
//        }
//        //管理员看所有
//        String admin = "admin";
//        //班组长看组内
//        String banzhang = "banzhang";
//        Integer roleType = 3;
//
//        if (roleList.contains(admin)){
//            roleType = 1;
//        }
//        if (roleList.contains(banzhang)){
//            roleType = 2;
//        }

//        final List<Station> stationList = stationService.list(new QueryWrapper<Station>().eq("team_id", user.getOrgId()).eq("del_flag", 0));
//        final List<Integer> orgIds = stationList.stream().map(Station::getId).collect(Collectors.toList());
//
//        QueryWrapper<RepairPool> unassignwrapper = new QueryWrapper<>();
//        unassignwrapper.ge("start_time", startTime.concat(" 00:00:00"))
//                .le("end_time", endTime.concat(" 23:59:59"))
//                .eq("del_flag", 0).eq("status",0);
//        if (roleType == 2 || roleType == 3){
//            unassignwrapper.in("organization_id", orgIds);
//        }
//        //该员工所在站点的所有未指派的检修池任务
//        List<RepairPool> unassign = repairPoolMapper.selectList(unassignwrapper);
//        final Map<String, List<RepairPool>> unassignMap = unassign.stream().collect(Collectors.groupingBy(RepairPool::getOrganizationId));
//        for (Map.Entry<String, List<RepairPool>> entry : unassignMap.entrySet()) {
//            final String stationId = entry.getKey();
//            final Station station = stationService.getById(stationId);
//            final List<RepairPool> list = entry.getValue();
//            RepairPool repairPool = list.get(0);
//            List copyList = MyBeanUtils.copyList(list, RepairPoolListVO.class);
//            if (repairPool.getStatus() == 0) {
//                ReTaskDetailVO vo = new ReTaskDetailVO();
//                vo.setWeeks(repairPool.getWeeks());
//                vo.setTeamName(user.getOrgName());
//                vo.setStatus(repairPool.getStatus());
//                vo.setStartTime(repairPool.getStartTime());
//                vo.setEndTime(repairPool.getEndTime());
//                vo.setRepairPoolList(copyList);
//                vo.setStationName(station.getStationName());
//                vo.setLineName(station.getLineName());
//                reTaskDetailVOS.add(vo);
//            }
//        }

    //已指派
//        QueryWrapper<RepairPool> assignwrapper = new QueryWrapper<>();
//        assignwrapper
//                .ge("start_time", startTime.concat(" 00:00:00"))
//                .le("end_time", endTime.concat(" 23:59:59"))
//                .eq("del_flag", 0).eq("status",1);
//        if (roleType == 2){
//            assignwrapper.in("organization_id", orgIds);
//        }
//        if (roleType == 3){
//            assignwrapper.apply(user.getId() != null, "find_in_set('" + user.getId() + "',repair_user_ids)");
//        }

//        List<RepairPool> assign = repairPoolMapper.selectList(assignwrapper);
//        final Map<String, List<RepairPool>> assignMap = assign.stream().collect(Collectors.groupingBy(RepairPool::getOrganizationId));
//        for (Map.Entry<String, List<RepairPool>> entry : assignMap.entrySet()) {
//            final String orgId = entry.getKey();
//            final List<RepairPool> list = entry.getValue();
//            final Station station = stationService.getById(orgId);
//            //如果有一条记录需要验收那么所有的任务需要验收
//            Integer isReceipt = 0;
//            for (RepairPool pool : list) {
//                if (pool.getIsReceipt() == 1) {
//                    isReceipt = 1;
//                    break;
//                }
//            }
//
//            final List receiptList = MyBeanUtils.copyList(list, RepairPoolListVO.class);
//            List<Long> collect = list.stream().map(RepairPool::getId).collect(Collectors.toList());
//            String repairPoolIds = StringUtils.join(collect, ",");
//            QueryWrapper<RepairTask> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("del_flag", 0).eq("repair_pool_ids", repairPoolIds).last("limit 1");
//            RepairTask repairTask = this.baseMapper.selectOne(queryWrapper);
//            if (repairTask == null) {
//                break;
//            }
//            ReTaskDetailVO vo = this.generateReTask(list.get(0), repairTask, user.getOrgName(), receiptList, isReceipt);
//            vo.setStationName(station.getStationName());
//            vo.setLineName(station.getLineName());
//            reTaskDetailVOS.add(vo);
//        }
//        result.setResult(reTaskDetailVOS);
//        return result;
//    }

//    @Override
//    public Result receiveByUser(LoginUser user, String ids, String workType, String planOrderCode, String planOrderCodeUrl) {
//        return repairPoolServiceImpl.assigned(ids, user.getId(), user.getRealname(), , workType, planOrderCode, planOrderCodeUrl);
//    }

    @Override
    @Transactional
    public Result commit(String id, String position, String content, String urls, String deviceIds, String processContent) {
        JSONArray array = new JSONArray();
        try {
            if (StrUtil.isNotBlank(deviceIds)){
                array = JSONUtil.parseArray(deviceIds);
            }
        } catch (Exception e) {
            throw new SwscException("设备json转换异常");
        }

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        RepairTask repairTask = this.baseMapper.selectById(id);
        if (repairTask == null) {
            return Result.error("非法参数ID");
        }
        if (repairTask.getStatus() != 0) {
            return Result.error("非法操作");
        }

        //不能执行以后的任务
        if (repairTask.getStartTime().compareTo(new Date()) > 0){
            return Result.error("无法执行以后的任务");
        }

        String deviceRecord = "";

        try {
            List<DeviceChangeSparePart> saveDepartData = new ArrayList<>();
            List<SparePartScrap> sparePartList = new ArrayList<>();
            List<SparePartStock> updateStockList = new ArrayList<>();
            //库存
            Map<String, SparePartStock> stockMap = null;
            List<SparePartStock> partStockList = partStockService.lambdaQuery().eq(SparePartStock::getOrgId, user.getOrgId()).eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0).list();
            if (partStockList!=null){
                stockMap = partStockList.stream().collect(Collectors.toMap(SparePartStock::getMaterialCode, p -> p));
            }

            //添加设备信息
            if(array!=null&&array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    final Long deviceId = array.getJSONObject(i).getLong("deviceId");
                    final String oldSparePartCode = array.getJSONObject(i).getStr("oldSparePartCode");
                    final Integer oldSparePartNum = array.getJSONObject(i).getInt("oldSparePartNum");
                    final DeviceChangeSparePart deviceChangeSparePart = new DeviceChangeSparePart();
                    deviceChangeSparePart.setType(1);
                    deviceChangeSparePart.setCode(repairTask.getId().toString());
                    deviceChangeSparePart.setDeviceId(deviceId);
                    deviceChangeSparePart.setOldSparePartCode(oldSparePartCode);
                    deviceChangeSparePart.setOldSparePartNum(oldSparePartNum);
                    deviceChangeSparePart.setNewSparePartCode(oldSparePartCode);
                    deviceChangeSparePart.setNewSparePartNum(oldSparePartNum);
                    saveDepartData.add(deviceChangeSparePart);

                    //备件报损
                    SparePartScrap sparePartScrap = new SparePartScrap();
                    sparePartScrap.setMaterialCode(deviceChangeSparePart.getOldSparePartCode())
                            .setNum(deviceChangeSparePart.getOldSparePartNum())
                            .setStatus(0).setScrapTime(new Date())
                            .setReason("检修更换备件报损")
                            .setDelFlag(CommonConstant.DEL_FLAG_0);
                    sparePartList.add(sparePartScrap);


                    //处理班组数量
                    if (deviceChangeSparePart.getOldSparePartNum()!=null) {
                        if (stockMap != null && stockMap.get(deviceChangeSparePart.getOldSparePartCode()) != null) {
                            SparePartStock stock = stockMap.get(deviceChangeSparePart.getOldSparePartCode());
                            stock.setNum(stock.getNum() != null ? stock.getNum() - deviceChangeSparePart.getOldSparePartNum() : -deviceChangeSparePart.getOldSparePartNum());
                            updateStockList.add(stock);
                        } else {
                            SparePartStock stock = new SparePartStock();
                            stock.setMaterialCode(deviceChangeSparePart.getCode());
                            stock.setNum(-deviceChangeSparePart.getOldSparePartNum());
                            stock.setOrgId(user.getOrgId()).setDelFlag(0);
                            updateStockList.add(stock);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(saveDepartData)) {
                deviceChangeSparePartService.saveBatch(saveDepartData);
                if (CollectionUtils.isNotEmpty(saveDepartData)) {
                    deviceRecord = StringUtils.join(saveDepartData.stream().map(DeviceChangeSparePart::getId).collect(Collectors.toList()), ",");
                }
                if (CollectionUtils.isNotEmpty(sparePartList)) {
                    sparePartScrapService.saveBatch(sparePartList);
                }
                if (CollectionUtils.isNotEmpty(updateStockList)) {
                    partStockService.saveOrUpdateBatch(updateStockList);
                }
            }
        } catch (Exception e) {
            log.error("检修单提交:解析device设备异常：", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("设备json解析异常");
        }

        repairTask.setStatus(1);
        repairTask.setPosition(position);
        repairTask.setProcessContent(processContent);
        repairTask.setContent(content);
        repairTask.setSubmitUserId(user.getId());
        repairTask.setSumitUserName(user.getRealname());
        repairTask.setSubmitTime(new Date());

        //添加需要更换的设备ids
        repairTask.setDeviceIds(deviceRecord);
        this.baseMapper.updateById(repairTask);

        String[] split = urls.split(",");
        for (String url : split) {
            RepairTaskEnclosure repairTaskEnclosure = new RepairTaskEnclosure();
            repairTaskEnclosure.setUrl(url);
            repairTaskEnclosure.setParentId(repairTask.getId());
            repairTaskEnclosureMapper.insert(repairTaskEnclosure);
        }
        final String staffIds = repairTask.getStaffIds();
        final String[] strings = staffIds.split(",");
        final List<String> userIdList = Arrays.asList(strings);
        ArrayList newList = new ArrayList<>(userIdList);
        newList.remove(user.getId());
        if (newList != null && newList.size() > 0) {
            userTaskService.removeUserTaskWork(newList, repairTask.getId(), UserTaskConstant.USER_TASK_TYPE_2);
        }
        userTaskService.complete(user.getId(), repairTask.getId(), UserTaskConstant.USER_TASK_TYPE_2);
        return Result.ok();
    }

    @Override
    public Result<RepairRecordVO> getRepairTaskByUserIdAndTime(String userId, String time) {
        Result<RepairRecordVO> result = new Result<>();
        LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(RepairTask::getStartTime, time+" 00:00:00").ge(RepairTask::getEndTime, time+" 23:59:59")
                //.eq(RepairTask::getStatus, 0)
                .eq(RepairTask::getDelFlag, CommonConstant.DEL_FLAG_0).like(RepairTask::getStaffIds, userId).last("limit 1");
        RepairTask repairTask = this.baseMapper.selectOne(wrapper);
        if (repairTask == null) {
            return result.error500("暂无记录");
        }
        LambdaQueryWrapper<RepairPool> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(RepairPool::getRepairPoolContent).eq(RepairPool::getDelFlag, 0)
                .in(RepairPool::getId, repairTask.getRepairPoolIds());
        List<RepairPool> list = repairPoolMapper.selectList(queryWrapper);
        if (list.size() == 0) {
            return result.error500("暂无记录");
        }
        List<String> collect = list.stream().map(RepairPool::getRepairPoolContent).collect(Collectors.toList());
        RepairRecordVO vo = new RepairRecordVO();
        vo.setRepairTaskCode(repairTask.getCode());
        vo.setRepairPoolContent(collect);
        result.setResult(vo);
        return result;
    }

    @Override
    public Result queryByDevice(DeviceQueryVO deviceQueryVO) {
        Result<IPage<RepairTask>> result = new Result<IPage<RepairTask>>();
        final LambdaQueryWrapper<RepairTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply(deviceQueryVO.getDeviceId() != null, "find_in_set(" + deviceQueryVO.getDeviceId() + ",device_ids)");
//        wrapper.like(RepairTask::getDeviceIds,deviceQueryVO.getDeviceId());
        if (deviceQueryVO.getWeeks() != null) {
            wrapper.eq(RepairTask::getWeeks, deviceQueryVO.getWeeks());
        }
        if (StrUtil.isNotBlank(deviceQueryVO.getRepairName())) {
            wrapper.like(RepairTask::getSumitUserName, deviceQueryVO.getRepairName());
        }
        wrapper.eq(RepairTask::getDelFlag, 0);
        Page<RepairTask> page = new Page<RepairTask>(deviceQueryVO.getPageNo(), deviceQueryVO.getPageSize());
        IPage<RepairTask> pageList = this.baseMapper.selectPage(page, wrapper);
        final List<RepairTask> list = pageList.getRecords();
        list.forEach(x -> {
            final String stationId = x.getOrganizationId();
            final Station station = stationService.getById(stationId);
            if (station != null) {
                x.setLineName(station.getStationName());
            }
        });
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @Override
    public boolean callback(Long repairId, String code) {
        RepairTask task = this.getById(repairId);
        if (task==null){
            return false;
        }
        task.setFaultCode(code);
        return this.updateById(task);
    }

    /**
     * 生成检修单任务
     *
     * @param repairPool
     * @param repairTask
     * @param departName
     * @param list
     * @param isReceipt
     * @return
     */
    private ReTaskDetailVO generateReTask(RepairPool repairPool, RepairTask repairTask, String departName, List<RepairPoolListVO> list, Integer isReceipt) {
        ReTaskDetailVO vo = new ReTaskDetailVO();
        vo.setTaskId(repairTask.getId());
        vo.setTaskCode(repairTask.getCode());
        vo.setStartTime(repairPool.getStartTime());
        vo.setEndTime(repairPool.getEndTime());
        vo.setWeeks(repairPool.getWeeks());
        vo.setStaffNames(repairTask.getStaffNames());
        vo.setSubmitTime(Optional.ofNullable(repairTask.getSubmitTime()).orElse(null));
        vo.setStatus(repairTask.getStatus());
        vo.setIsReceipt(isReceipt);
        vo.setTeamName(departName);
        vo.setPosition(Optional.ofNullable(repairTask.getPosition()).orElse(null));
        vo.setRepairPoolList(list);
        vo.setErrorContent(repairTask.getErrorContent());
        vo.setContent(Optional.ofNullable(repairTask.getContent()).orElse(null));
        vo.setProcessContent(Optional.ofNullable(repairTask.getProcessContent()).orElse(null));
        vo.setSumitUserName(Optional.ofNullable(repairTask.getSumitUserName()).orElse(null));
        vo.setConfirmUserName(Optional.ofNullable(repairTask.getConfirmUserName()).orElse(null));
        vo.setConfirmTime(Optional.ofNullable(repairTask.getConfirmTime()).orElse(null));
        vo.setReceiptUserName(Optional.ofNullable(repairTask.getReceiptUserName()).orElse(null));
        vo.setReceiptTime(Optional.ofNullable(repairTask.getReceiptTime()).orElse(null));
        vo.setWorkType(Optional.ofNullable(repairTask).orElse(new RepairTask()).getWorkType());
        vo.setPlanOrderCode(Optional.ofNullable(repairTask.getPlanOrderCode()).orElse(null));
        vo.setPlanOrderCodeUrl(Optional.ofNullable(repairTask.getPlanOrderCodeUrl()).orElse(null));

        vo.setFaultCode(repairTask.getFaultCode());

        List<RepairTaskEnclosure> repairTaskEnclosureList = repairTaskEnclosureMapper.selectList(new LambdaQueryWrapper<RepairTaskEnclosure>().eq(RepairTaskEnclosure::getParentId, repairTask.getId()).eq(RepairTaskEnclosure::getDelFlag, 0));
        List<String> collect = repairTaskEnclosureList.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList());
        vo.setUrl(collect);
        return vo;
    }
}
