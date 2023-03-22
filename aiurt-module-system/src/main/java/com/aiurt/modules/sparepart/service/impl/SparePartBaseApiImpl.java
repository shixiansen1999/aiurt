package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.fault.dto.SparePartStockDTO;
import com.aiurt.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.modules.fault.service.IDeviceChangeSparePartService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.sparepart.dto.DeviceChangeSparePartDTO;
import com.aiurt.modules.sparepart.entity.*;
import com.aiurt.modules.sparepart.mapper.*;
import com.aiurt.modules.sparepart.service.*;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISparePartBaseApi;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Service
public class SparePartBaseApiImpl implements ISparePartBaseApi {

    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;

    @Autowired
    private ISysBaseAPI sysBaseApi;

    @Autowired
    private ISparePartReplaceService partReplaceService;

    @Autowired
    private ISparePartMalfunctionService sparePartMalfunctionService;

    @Autowired
    private IDeviceAssemblyService deviceAssemblyService;

    @Autowired
    private IMaterialBaseService materialBaseService;

    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISparePartOutOrderService outOrderService;

    @Autowired
    private ISparePartScrapService sparePartScrapService;
    @Autowired
    private IDeviceChangeSparePartService sparePartService;

    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;
    @Autowired
    private ISparePartStockService sparePartStockService;
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    @Autowired
    private SparePartInOrderMapper sparePartInOrderMapper;
    @Autowired
    private MaterialBaseMapper materialBaseMapper;
    @Autowired
    @Lazy
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    @Autowired
    private SparePartLendMapper sparePartLendMapper;



    /**
     * 更新出库单未使用的数量
     *
     * @param updateMap
     */
    @Override
    public void updateSparePartOutOrder(Map<String, Integer> updateMap) {
        if (Objects.nonNull(updateMap) && updateMap.size() > 0) {
            updateMap.forEach((id, num) -> {
                if (StrUtil.isNotBlank(id) && Objects.nonNull(num)) {
                    sparePartOutOrderMapper.updateSparePartOutOrderUnused(id, num);
                }
            });
        }
    }

    /**
     * 处理备件更换
     *
     * @param sparePartList
     */
    @Override
    public void dealChangeSparePart(List<DeviceChangeSparePartDTO> sparePartList) {
        log.info("处理备件更换流程->{}", JSONObject.toJSONString(sparePartList));
        // 1.插入数据 ：需要往spare_part_malfunction备件履历表、spare_part_replace备件更换记录表、备件报废表spare_part_scrap插入数据
        // spare_part_malfunction备件履历表
        List<SparePartReplace> list = new ArrayList<>();
        List<SparePartMalfunction> malfunctionList = new ArrayList<>();
        List<SparePartScrap> sparePartScrapList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sparePartList)) {
            List<DeviceAssembly> deviceAssemblyList = deviceAssemblyService.list();
            Set<String> assemblyCodeSet = deviceAssemblyList.stream().map(DeviceAssembly::getCode).collect(Collectors.toSet());
            // 插入到组件表中
            sparePartList.stream().forEach(deviceChange -> {
                // 线处理
                String faultCode = deviceChange.getCode();
                // 备件更换记录表
                String outOrderId = deviceChange.getOutOrderId();

                if (StrUtil.isBlank(outOrderId)) {
                    outOrderService.getById(outOrderId);
                }
                String deviceCode = deviceChange.getDeviceCode();
                LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Device::getCode, deviceCode).eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
                Device device = deviceService.getBaseMapper().selectOne(queryWrapper);
                if (Objects.isNull(device)) {
                    return;
                }
                // 原组件
                String oldSparePartCode = deviceChange.getOldSparePartCode();
                String newSparePartCode = deviceChange.getNewSparePartCode();
                LambdaQueryWrapper<MaterialBase> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MaterialBase::getCode, newSparePartCode).eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0).last("limit 1");
                MaterialBase materialBase = materialBaseService.getBaseMapper().selectOne(wrapper);
                if (Objects.isNull(materialBase)) {
                    return;
                }
                Integer newSparePartNum = Optional.ofNullable(deviceChange.getNewSparePartNum()).orElse(1);
                for (int i = 0; i < newSparePartNum; i++) {
                    String key = "";
                    int num = 0;
                    do {
                        String number = String.format("%03d", num);
                        key = newSparePartCode + number;
                        num = num + 1;
                    } while (assemblyCodeSet.contains(key));
                    assemblyCodeSet.add(key);
                    // 组件
                    DeviceAssembly deviceAssembly = new DeviceAssembly();
                    deviceAssembly.setBaseTypeCode(materialBase.getBaseTypeCode());
                    deviceAssembly.setStatus("0");
                    deviceAssembly.setCode(key);
                    deviceAssembly.setManufactorCode(materialBase.getManufactorCode());
                    deviceAssembly.setDeviceCode(deviceCode);
                    deviceAssembly.setMaterialName(materialBase.getName());
                    deviceAssembly.setMaterialCode(materialBase.getCode());
                    deviceAssembly.setSpecifications(materialBase.getSpecifications());
                    deviceAssembly.setDelFlag(0);
                    deviceAssembly.setStartDate(new Date());
                    deviceAssembly.setDeviceTypeCode(device.getDeviceTypeCode());
                    deviceAssembly.setBuyDate(new Date());
                    deviceAssembly.setOnlineDate(new Date());
                    deviceAssembly.setUnit(materialBase.getUnit());
                    deviceAssemblyService.save(deviceAssembly);
                    // spare_part_replace备件更换记录表
                    SparePartReplace replace = new SparePartReplace();
                    replace.setMaintenanceRecord(faultCode);
                    replace.setMaterialsCode(newSparePartCode);
                    replace.setOutOrderId(outOrderId);
                    replace.setDelFlag(CommonConstant.DEL_FLAG_0);
                    // 被替换的组件
                    replace.setReplaceSubassemblyCode(oldSparePartCode);
                    replace.setSubassemblyCode(key);
                    list.add(replace);
                }
                if (StrUtil.isNotBlank(oldSparePartCode)) {

                    LambdaQueryWrapper<DeviceAssembly> assemblyLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    assemblyLambdaQueryWrapper.eq(DeviceAssembly::getDeviceCode, deviceCode)
                            .eq(DeviceAssembly::getCode, oldSparePartCode).last("limit 1");
                    DeviceAssembly deviceAssembly = deviceAssemblyService.getBaseMapper().selectOne(assemblyLambdaQueryWrapper);
                    // 查询当前替换人员的仓库.
                    SparePartStockInfo stockInfo = sparePartStockInfoService.getSparePartStockInfoByUserName(deviceChange.getCreateBy());
                    if (Objects.nonNull(deviceAssembly)) {
                        // 更新状态
                        deviceAssembly.setDelFlag(CommonConstant.DEL_FLAG_1);
                        deviceAssembly.setStatus("1");
                        deviceAssemblyService.updateById(deviceAssembly);

                        // 备件报废表spare_part_scrap插入数据
                        SparePartScrap sparePartScrap = new SparePartScrap();
                        sparePartScrap.setNumber("1");
                        sparePartScrap.setMaterialCode(deviceAssembly.getMaterialCode());
                        sparePartScrap.setWarehouseCode(Objects.isNull(stockInfo) ? "" : stockInfo.getWarehouseCode());
                        sparePartScrap.setOutOrderId(outOrderId);
                        sparePartScrap.setName(deviceAssembly.getMaterialName());
                        sparePartScrap.setNum(1);
                        sparePartScrap.setScrapTime(deviceChange.getCreateTime());
                        sparePartScrap.setCreateBy(deviceChange.getCreateBy());
                        sparePartScrap.setStatus(1);
                        sparePartScrap.setLineCode(device.getLineCode());
                        sparePartScrap.setStationCode(device.getStationCode());
                        sparePartScrap.setKeepPerson(deviceChange.getCreateBy());
                        sparePartScrap.setBuyTime(deviceAssembly.getBuyDate());
                        sparePartScrap.setDelFlag(0);
                        sparePartScrap.setCreateTime(new Date());
                        sparePartScrap.setMajorCode(device.getMajorCode());
                        sparePartScrap.setSystemCode(device.getSystemCode());
                        sparePartScrap.setBaseTypeCode(deviceAssembly.getBaseTypeCode());
                        sparePartScrapList.add(sparePartScrap);
                    }
                }
            });
            sparePartList.stream().forEach(deviceChangeDTO -> {
                // 线处理
                String faultCode = deviceChangeDTO.getCode();
                // 备件更换记录表
                String outOrderId = deviceChangeDTO.getOutOrderId();
                // 需要往spare_part_malfunction备件履历表
                String createBy = deviceChangeDTO.getCreateBy();
                LoginUser loginUser = sysBaseApi.getUserByName(createBy);
                SparePartMalfunction sparePartMalfunction = new SparePartMalfunction();
                sparePartMalfunction.setOutOrderId(outOrderId);
                sparePartMalfunction.setMaintenanceRecord(faultCode);
                sparePartMalfunction.setMalfunctionDeviceCode(deviceChangeDTO.getDeviceCode());
                sparePartMalfunction.setMalfunctionType(1);
                sparePartMalfunction.setDescription("");
                sparePartMalfunction.setReplaceNumber(deviceChangeDTO.getNewSparePartNum());
                if (Objects.nonNull(loginUser)) {
                    sparePartMalfunction.setOrgId(loginUser.getOrgId());
                }
                sparePartMalfunction.setMaintainUserId(createBy);
                sparePartMalfunction.setMaintainTime(new Date());
                sparePartMalfunction.setDelFlag(0);
                malfunctionList.add(sparePartMalfunction);
            });
        }
        if (CollectionUtil.isNotEmpty(list)) {
            partReplaceService.saveBatch(list);
        }
        // 保存数据
        if (CollectionUtil.isNotEmpty(malfunctionList)) {
            sparePartMalfunctionService.saveBatch(malfunctionList);
        }
        if (CollectionUtil.isNotEmpty(sparePartScrapList)) {
            sparePartScrapService.saveBatch(sparePartScrapList);
        }
    }

    @Override
    public void addSparePartOutOrder(List<SparePartStockDTO> dtoList, String faultCode) {
        if (CollUtil.isNotEmpty(dtoList)) {
            //id为空即为新增的，不为空即旧数据
            List<SparePartStockDTO> exitFaultSparePartList = dtoList.stream().filter(d -> ObjectUtil.isNotEmpty(d.getId())).collect(Collectors.toList());
            List<SparePartStockDTO> unExitFaultSparePartList = dtoList.stream().filter(d -> ObjectUtil.isEmpty(d.getId())).collect(Collectors.toList());
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            SparePartStockInfo stockInfo = new SparePartStockInfo();
            // 获取当前登录人所属机构， 根据所属机构擦查询管理二级管理仓库
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            // 查询仓库
            LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
            if (ObjectUtil.isNotEmpty(loginUser.getOrgId())) {
                //一个班组管理一个仓库，用selectOne,防止有人多配，只取一条
                wrapper.eq(SparePartStockInfo::getOrganizationId, loginUser.getOrgId()).last("limit 1");
                stockInfo = sparePartStockInfoService.getOne(wrapper);
            } else {
                throw new AiurtBootException(" 该用户没绑定机构,无法进行组件或易耗品更换");
            }
            if (ObjectUtil.isEmpty(stockInfo)) {
                throw new AiurtBootException(" 该用户所在的班组无备件仓库,无法进行组件或易耗品更换");
            }
            //旧数据进行判断，是否有移除的,有的话找到出来，然后恢复之前的
            if (CollUtil.isNotEmpty(exitFaultSparePartList)) {
                List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(new LambdaQueryWrapper<DeviceChangeSparePart>().eq(DeviceChangeSparePart::getCode, faultCode));
                List<String> deviceFaultIdList = exitFaultSparePartList.stream().map(SparePartStockDTO::getId).collect(Collectors.toList());
                deviceChangeSparePartList = deviceChangeSparePartList.stream().filter(d ->!deviceFaultIdList.contains(d.getId())).collect(Collectors.toList());
                recoverSparePart(deviceChangeSparePartList);
            }
            //新数据进行判断，是否是自己的仓库
            if (CollUtil.isNotEmpty(unExitFaultSparePartList)) {
                if (CollUtil.isEmpty(exitFaultSparePartList)){
                //旧数据进行判断，是否有移除的,有的话找到出来，然后恢复之前的
                List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(new LambdaQueryWrapper<DeviceChangeSparePart>().eq(DeviceChangeSparePart::getCode, faultCode));
                recoverSparePart(deviceChangeSparePartList);
                }
                for (SparePartStockDTO lendStockDTO : unExitFaultSparePartList) {
                    DeviceChangeSparePart sparePart = new DeviceChangeSparePart();
                    sparePart.setCode(faultCode);
                    sparePart.setNewOrgCode(user.getOrgCode());
                    sparePart.setOldSparePartCode(lendStockDTO.getOldSparePartCode());
                    sparePart.setNewSparePartCode(lendStockDTO.getMaterialCode());
                    sparePart.setConsumables(lendStockDTO.getConsumablesType());
                    sparePart.setWarehouseCode(lendStockDTO.getWarehouseCode());
                    if ("0".equals(lendStockDTO.getConsumablesType())) {
                        SparePartScrap scrap = new SparePartScrap();
                        scrap.setStatus(1);
                        scrap.setSysOrgCode(user.getOrgCode());
                        scrap.setMaterialCode(lendStockDTO.getOldSparePartCode());
                        scrap.setWarehouseCode(lendStockDTO.getWarehouseCode());
                        scrap.setNum(1);
                        scrap.setFaultCode(faultCode);
                        scrap.setScrapTime(new Date());
                        sparePartScrapService.save(scrap);
                        try {
                            String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(user.getOrgCode()), Collections.singletonList(RoleConstant.FOREMAN));
                            //发送通知
                            MessageDTO messageDTO = new MessageDTO(user.getUsername(),userName, "备件报废申请-确认" + DateUtil.today(), null);
                            //构建消息模板
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, scrap.getId());
                            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE,  SysAnnmentTypeEnum.SPAREPART_LEND.getType());
                            map.put("materialCode",scrap.getMaterialCode());
                            String materialName= sysBaseApi.getMaterialNameByCode(scrap.getMaterialCode());
                            map.put("name",materialName);
                            map.put("num",scrap.getNum());
                            LoginUser userByName = sysBaseApi.getUserByName(scrap.getCreateBy());
                            map.put("realName",userByName.getRealname());
                            map.put("scrapTime", DateUtil.format(scrap.getScrapTime(),"yyyy-MM-dd HH:mm:ss"));
                            messageDTO.setData(map);
                            //发送待办
                            TodoDTO todoDTO = new TodoDTO();
                            todoDTO.setData(map);
                            SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
                            todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
                            todoDTO.setTitle("备件报废申请-确认" + DateUtil.today());
                            todoDTO.setMsgAbstract("备件报废申请");
                            todoDTO.setPublishingContent("备件报废申请，请确认");
                            todoDTO.setCurrentUserName(userName);
                            todoDTO.setBusinessKey(scrap.getId());
                            todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType());
                            todoDTO.setCurrentUserName(userName);
                            todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_SCRAP.getType());
                            todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
                            todoDTO.setTemplateCode(CommonConstant.SPAREPARTSCRAP_SERVICE_NOTICE);
                            isTodoBaseAPI.createTodoTask(todoDTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        sparePart.setScrapId(scrap.getId());
                        QueryWrapper<DeviceChangeSparePart> queryWrapper = new QueryWrapper();
                        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                        queryWrapper.orderByDesc("create_time");
                        queryWrapper.last("limit 1");
                        DeviceChangeSparePart changeSparePart = sparePartService.getOne(queryWrapper);
                        String format = "";
                        if (ObjectUtil.isNotEmpty(changeSparePart.getNewSparePartSplitCode())) {
                            String numstr = changeSparePart.getNewSparePartSplitCode().substring(changeSparePart.getNewSparePartSplitCode().length() - 4);
                            format = String.format("%04d", Long.parseLong(numstr) + 1);
                        } else {
                            format = "0001";
                        }
                        sparePart.setDeviceCode(lendStockDTO.getDeviceCode());
                        sparePart.setNewSparePartSplitCode(lendStockDTO.getMaterialCode() + format);
                    }
                    sparePart.setNewSparePartNum(lendStockDTO.getNewSparePartNum());
                    sparePart.setNewOrgCode(user.getOrgCode());
                    //自己仓库：生成出库单（待确认）
                    if (lendStockDTO.getWarehouseCode().equals(stockInfo.getWarehouseCode())) {
                        SparePartOutOrder lendOutOrder = new SparePartOutOrder();
                        lendOutOrder.setNum(lendStockDTO.getNewSparePartNum());
                        lendOutOrder.setWarehouseCode(lendStockDTO.getWarehouseCode());
                        lendOutOrder.setApplyOutTime(new Date());
                        lendOutOrder.setApplyUserId(user.getUsername());
                        lendOutOrder.setMaterialCode(lendStockDTO.getMaterialCode());
                        sparePartOutOrderMapper.insert(lendOutOrder);
                        sparePart.setLendOutOrderId(lendOutOrder.getId());
                        //发消息
                        sendOutboundMessages(lendOutOrder,user);
                    } else {
                        //1.生成借出单（已借出）
                        SparePartLend sparePartLend = new SparePartLend();
                        sparePartLend.setMaterialCode(lendStockDTO.getMaterialCode());
                        sparePartLend.setLendWarehouseCode(lendStockDTO.getWarehouseCode());
                        sparePartLend.setBackWarehouseCode(stockInfo.getWarehouseCode());
                        sparePartLend.setEntryOrgCode(user.getOrgCode());
                        SysDepart sysDepart = sysDepartService.getOne(new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag, 0).eq(SysDepart::getId, lendStockDTO.getOrgId()));
                        sparePartLend.setExitOrgCode(sysDepart.getOrgCode());
                        sparePartLend.setOutTime(new Date());
                        sparePartLend.setLendPerson(user.getUsername());
                        sparePartLend.setLendNum(lendStockDTO.getNewSparePartNum());
                        sparePartLend.setBorrowNum(lendStockDTO.getNewSparePartNum());
                        sparePartLend.setStatus(2);
                        sparePartLend.setCreateOrgCode(user.getOrgCode());
                        sparePartLendMapper.insert(sparePartLend);
                        sparePart.setLendOrderId(sparePartLend.getId());
                        //2.借出仓库库存数做减法
                        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode, sparePartLend.getMaterialCode()).eq(SparePartStock::getWarehouseCode, sparePartLend.getLendWarehouseCode()));
                        lendStock.setNum(lendStock.getNum() - sparePartLend.getLendNum());
                        sparePartStockMapper.updateById(lendStock);
                        sparePart.setLendInventoryOrderId(lendStock.getId());
                        //3.发消息（借出单的消息）
                        try {

                            MessageDTO messageDTO = new MessageDTO(user.getUsername(), user.getUsername(), "备件借出成功" + DateUtil.today(), null);
                            //构建消息模板
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, sparePartLend.getId());
                            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.SPAREPART_LEND.getType());
                            map.put("materialCode", sparePartLend.getMaterialCode());
                            String materialName = sysBaseApi.getMaterialNameByCode(sparePartLend.getMaterialCode());
                            map.put("name", materialName);
                            map.put("lendNum", sparePartLend.getLendNum());
                            String warehouseName = sysBaseApi.getWarehouseNameByCode(sparePartLend.getLendWarehouseCode());
                            map.put("warehouseName", warehouseName);

                            messageDTO.setData(map);
                            //业务类型，消息类型，消息模板编码，摘要，发布内容
                            messageDTO.setTemplateCode(CommonConstant.SPAREPARTLEND_SERVICE_NOTICE);
                            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE);
                            messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                            messageDTO.setMsgAbstract("备件借出申请确认");
                            messageDTO.setPublishingContent("备件借出申请通过");
                            messageDTO.setCategory(CommonConstant.MSG_CATEGORY_10);
                            sysBaseApi.sendTemplateMessage(messageDTO);
                            // 更新待办
                            isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.SPAREPART_LEND.getType(), sparePartLend.getId(), user.getUsername(), "1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //4.借出仓库生成出库单
                        SparePartOutOrder lendOutOrder = new SparePartOutOrder();
                        lendOutOrder.setMaterialCode(sparePartLend.getMaterialCode());
                        lendOutOrder.setWarehouseCode(sparePartLend.getLendWarehouseCode());
                        lendOutOrder.setSysOrgCode(user.getOrgCode());
                        lendOutOrder.setNum(sparePartLend.getLendNum());
                        lendOutOrder.setConfirmTime(new Date());
                        lendOutOrder.setConfirmUserId(user.getUsername());
                        lendOutOrder.setApplyOutTime(new Date());
                        lendOutOrder.setApplyUserId(sparePartLend.getLendPerson());
                        lendOutOrder.setStatus(CommonConstant.SPARE_PART_OUT_ORDER_STATUS_2);
                        sparePartOutOrderMapper.insert(lendOutOrder);
                        sparePart.setLendOutOrderId(lendOutOrder.getId());

                        //5.借入仓库库存数做加法

                        SparePartStock  borrowingStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode, lendOutOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode, stockInfo.getWarehouseCode()));
                        if (null != borrowingStock) {
                            borrowingStock.setNum(borrowingStock.getNum() + sparePartLend.getLendNum());
                            sparePartStockMapper.updateById(borrowingStock);
                            sparePart.setBorrowingInventoryOrderId(borrowingStock.getId());
                        } else {
                            //插入库存
                            SparePartStock partStock = new SparePartStock();
                            partStock.setMaterialCode(lendOutOrder.getMaterialCode());
                            partStock.setNum(lendOutOrder.getNum());
                            partStock.setWarehouseCode(lendOutOrder.getWarehouseCode());
                            partStock.setOrgId(user.getOrgId());
                            partStock.setSysOrgCode(user.getOrgCode());
                            sparePartStockMapper.insert(partStock);
                            sparePart.setBorrowingInventoryOrderId(partStock.getId());
                        }
                        //6.借入仓库生成入库记录
                        SparePartInOrder sparePartInOrder = new SparePartInOrder();
                        sparePartInOrder.setMaterialCode(lendOutOrder.getMaterialCode());
                        sparePartInOrder.setWarehouseCode(stockInfo.getWarehouseCode());
                        sparePartInOrder.setNum(lendOutOrder.getNum());
                        sparePartInOrder.setOrgId(user.getOrgId());
                        sparePartInOrder.setConfirmStatus(CommonConstant.SPARE_PART_IN_ORDER_STATUS_1);
                        sparePartInOrder.setConfirmId(user.getUsername());
                        sparePartInOrder.setConfirmTime(new Date());
                        sparePartInOrder.setSysOrgCode(user.getOrgCode());
                        sparePartInOrderMapper.insert(sparePartInOrder);
                        sparePart.setIntOrderId(sparePartInOrder.getId());
                        //7.生成借入仓库的出库记录（待确认）
                        SparePartOutOrder borrowingOutOrder = new SparePartOutOrder();
                        borrowingOutOrder.setMaterialCode(sparePartInOrder.getMaterialCode());
                        borrowingOutOrder.setWarehouseCode(sparePartInOrder.getWarehouseCode());
                        borrowingOutOrder.setSysOrgCode(user.getOrgCode());
                        borrowingOutOrder.setNum(sparePartInOrder.getNum());
                        borrowingOutOrder.setApplyOutTime(new Date());
                        borrowingOutOrder.setApplyUserId(user.getUsername());
                        sparePartOutOrderMapper.insert(borrowingOutOrder);
                        sparePart.setBorrowingOutOrderId(borrowingOutOrder.getId());
                        //发消息
                        sendOutboundMessages(borrowingOutOrder,user);
                    }
                    sparePartService.getBaseMapper().insert(sparePart);
                }
            }
        } else {
            List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(new LambdaQueryWrapper<DeviceChangeSparePart>().eq(DeviceChangeSparePart::getCode, faultCode));
            recoverSparePart(deviceChangeSparePartList);
        }

    }

    public  void recoverSparePart(List<DeviceChangeSparePart> deviceChangeSparePartList){
        //移除之前添加的数据（借出、出入库单，相应的库存恢复）
        if (CollUtil.isNotEmpty(deviceChangeSparePartList)) {
            for (DeviceChangeSparePart part : deviceChangeSparePartList) {
                if(part.getScrapId()!=null) {
                    SparePartScrap scrap = sparePartScrapService.getById(part.getScrapId());
                    if(scrap.getStatus()==1){
                        sparePartScrapService.removeById(scrap);
                    }
                }
                //入库单不为空，即为借出流程
                if (ObjectUtil.isNotEmpty(part.getIntOrderId())) {
                    SparePartOutOrder borrowingOutOrder = sparePartOutOrderMapper.selectOne(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getId, part.getBorrowingOutOrderId()));
                    if (borrowingOutOrder.getStatus() == 1) {
                        //删除借入仓库的出库单
                        sparePartOutOrderMapper.deleteById(borrowingOutOrder);
                        //借入仓库库存扣减,删除入库单
                        SparePartInOrder sparePartInOrder = sparePartInOrderMapper.selectOne(new LambdaQueryWrapper<SparePartInOrder>().eq(SparePartInOrder::getId, part.getIntOrderId()));
                        SparePartStock borrowingStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getId, part.getBorrowingInventoryOrderId()));
                        borrowingStock.setNum(borrowingStock.getNum() - sparePartInOrder.getNum());
                        sparePartStockMapper.updateById(borrowingStock);
                        sparePartInOrderMapper.deleteById(sparePartInOrder);
                        //借出仓库库存加回,删除借出仓库的出库单、删除借出单
                        SparePartOutOrder lendOutOrder = sparePartOutOrderMapper.selectOne(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getId, part.getLendOutOrderId()));
                        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getId, part.getLendInventoryOrderId()));
                        lendStock.setNum(lendStock.getNum() + lendOutOrder.getNum());
                        sparePartStockMapper.updateById(lendStock);
                        sparePartOutOrderMapper.deleteById(lendOutOrder);
                        SparePartLend sparePartLend = sparePartLendMapper.selectOne(new LambdaQueryWrapper<SparePartLend>().eq(SparePartLend::getId, part.getLendOrderId()));
                        sparePartLendMapper.deleteById(sparePartLend);
                    }
                }
                //反则出库流程
                else {
                    SparePartOutOrder sparePartOutOrder = sparePartOutOrderMapper.selectOne(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getId, part.getLendOutOrderId()));
                    if (sparePartOutOrder.getStatus() == 1) {
                        sparePartOutOrderMapper.deleteById(sparePartOutOrder);
                    }
                }
            }
            //删除故障更换记录
            sparePartService.removeBatchByIds(deviceChangeSparePartList);
        }
    }
    //出库消息发送
    public  void sendOutboundMessages(SparePartOutOrder lendOutOrder,LoginUser user){
        try {
            //根据仓库编号获取仓库组织机构code
            String orgCode = sysBaseApi.getDepartByWarehouseCode(lendOutOrder.getWarehouseCode());
            String userName = sysBaseApi.getUserNameByDeptAuthCodeAndRoleCode(Collections.singletonList(orgCode), Collections.singletonList(RoleConstant.FOREMAN));
            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(), userName, "备件库出库申请" + DateUtil.today(), null);
            //构建消息模板
            HashMap<String, Object> map = new HashMap<>();
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, lendOutOrder.getId());
            map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.SPAREPART_OUT.getType());
            map.put("materialCode", lendOutOrder.getMaterialCode());
            String materialName = sysBaseApi.getMaterialNameByCode(lendOutOrder.getMaterialCode());
            map.put("name", materialName);
            map.put("num", lendOutOrder.getNum());
            String warehouseName = sysBaseApi.getWarehouseNameByCode(lendOutOrder.getWarehouseCode());
            map.put("warehouseName", warehouseName);
            map.put("realName", user.getRealname());
            messageDTO.setData(map);
            //发送待办
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setData(map);
            SysParamModel sysParamModelTodo = iSysParamAPI.selectByCode(SysParamCodeConstant.SPAREPART_MESSAGE_PROCESS);
            todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModelTodo) ? sysParamModelTodo.getValue() : "");
            todoDTO.setTitle("备件库出库申请" + DateUtil.today());
            todoDTO.setMsgAbstract("备件库出库申请");
            todoDTO.setPublishingContent("备件出库申请，请确认");
            todoDTO.setCurrentUserName(userName);
            todoDTO.setBusinessKey(lendOutOrder.getId());
            todoDTO.setBusinessType(TodoBusinessTypeEnum.SPAREPART_OUT.getType());
            todoDTO.setCurrentUserName(userName);
            todoDTO.setTaskType(TodoBusinessTypeEnum.SPAREPART_OUT.getType());
            todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
            todoDTO.setTemplateCode(CommonConstant.SPAREPARTOUTORDER_SERVICE_NOTICE);
            isTodoBaseAPI.createTodoTask(todoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
