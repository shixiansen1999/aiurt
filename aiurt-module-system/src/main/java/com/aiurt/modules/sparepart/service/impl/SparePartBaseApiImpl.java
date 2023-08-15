package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.service.ISysDepartService;
import com.aiurt.modules.system.service.ISysUserService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
    @Autowired
    private SparePartStockNumMapper sparePartStockNumMapper;
    @Autowired
    private ISysUserService userService;



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
        List<SparePartMalfunction> malfunctionList = new ArrayList<>();
        List<DeviceChangeSparePart> deviceChangeSpareParts = new ArrayList<>();
        List<SparePartReplace> replaceList = new ArrayList<>();
        List<DeviceAssembly> deleteAssemblyList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(sparePartList)) {

            List<String> materialBaseCodes = sparePartList.stream().map(DeviceChangeSparePartDTO::getMaterialBaseCode).collect(Collectors.toList());
            List<MaterialBase> materialBases = materialBaseService.list(new LambdaQueryWrapper<MaterialBase>()
                    .eq(MaterialBase::getDelFlag, CommonConstant.DEL_FLAG_0).in(MaterialBase::getCode,materialBaseCodes));
            Map<String, MaterialBase> baseMap = materialBases.stream().collect(Collectors.toMap(MaterialBase::getCode,Function.identity(), (m1,m2) -> m2));

            List<String> deviceCodes = sparePartList.stream().map(DeviceChangeSparePartDTO::getDeviceCode).collect(Collectors.toList());
            List<Device> deviceList = deviceService.list(new LambdaQueryWrapper<Device>()
                    .eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0).in(Device::getCode,deviceCodes));
            Map<String, Device> deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getCode, Function.identity(), (m1,m2) -> m2));

            List<String> createBys = sparePartList.stream().map(DeviceChangeSparePartDTO::getCreateBy).collect(Collectors.toList());
            List<SysUser> loginUsers = userService.list(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getDelFlag,CommonConstant.DEL_FLAG_0).in(SysUser::getUsername,createBys));
            Map<String, SysUser> userMap = loginUsers.stream().collect(Collectors.toMap(SysUser::getUsername, Function.identity(), (m1,m2) -> m2));

            // 插入到组件表中
            sparePartList.stream().forEach(deviceChange -> {
                // 线处理
                String faultCode = deviceChange.getCode();
                // 备件更换记录表
                String outOrderId = null;
                if(ObjectUtil.isNotEmpty(deviceChange.getBorrowingOutOrderId())){
                    outOrderId = deviceChange.getBorrowingOutOrderId();
                }else {
                    outOrderId = deviceChange.getLendOutOrderId();
                }
                if (StrUtil.isBlank(outOrderId)) {
                    outOrderService.getById(outOrderId);
                }
                String deviceCode = deviceChange.getDeviceCode();
                Device device = deviceMap.get(deviceCode);
                if (Objects.isNull(device)) {
                    return;
                }
                // 原组件
                String oldSparePartCode = deviceChange.getOldSparePartCode();
                String materialBaseCode = deviceChange.getMaterialBaseCode();
                String newSparePartSplitCode = deviceChange.getNewSparePartSplitCode();
                List<String> codes = StrUtil.splitTrim(newSparePartSplitCode, ",");
                MaterialBase materialBase = baseMap.get(materialBaseCode);
                if (Objects.isNull(materialBase)) {
                    return;
                }
                Integer newSparePartNum = Optional.ofNullable(deviceChange.getNewSparePartNum()).orElse(0);
                List<String> splitCodes = new ArrayList<>();
                for (int i = 0; i < newSparePartNum; i++) {
                    String key = codes.get(i);
                    //查询数据库是否存在该编码
                    DeviceAssembly assembly = deviceAssemblyService.getBaseMapper().selectOne(new LambdaQueryWrapper<DeviceAssembly>()
                            .eq(DeviceAssembly::getDelFlag,CommonConstant.DEL_FLAG_0).eq(DeviceAssembly::getCode,key));
                    if(ObjectUtil.isNotEmpty(assembly)){
                        int num = 1;
                        String format = "";
                        DeviceAssembly deviceAssembly = new DeviceAssembly();
                        do {
                            String number = String.format("%04d", num);
                            format = materialBase.getCode() + number;
                            //生成的编码是否在数据库已经被生成
                            deviceAssembly = deviceAssemblyService.getBaseMapper().selectOne(new LambdaQueryWrapper<DeviceAssembly>()
                                    .eq(DeviceAssembly::getDelFlag,CommonConstant.DEL_FLAG_0).eq(DeviceAssembly::getCode,format));
                            num = num + 1;
                        } while (ObjectUtil.isNotEmpty(deviceAssembly));
                        key = format;
                    }
                    splitCodes.add(key);
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
                }
                DeviceChangeSparePart deviceChangeSparePart = new DeviceChangeSparePart();
                String newSpitCode = CollUtil.join(splitCodes, ",");
                deviceChange.setNewSparePartSplitCode(newSpitCode);
                deviceChange.setNewSparePartCode(newSpitCode);
                BeanUtils.copyProperties(deviceChange,deviceChangeSparePart);
                deviceChangeSpareParts.add(deviceChangeSparePart);
                // spare_part_replace备件更换记录表
                SparePartReplace replace = new SparePartReplace();
                replace.setMaintenanceRecord(faultCode);
                replace.setMaterialsCode(materialBase.getCode());
                replace.setOutOrderId(outOrderId);
                replace.setDelFlag(CommonConstant.DEL_FLAG_0);
                // 被替换的组件
                replace.setReplaceSubassemblyCode(oldSparePartCode);
                replace.setSubassemblyCode(newSpitCode);
                replaceList.add(replace);
                if (StrUtil.isNotBlank(oldSparePartCode)) {
                    LambdaQueryWrapper<DeviceAssembly> assemblyLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    assemblyLambdaQueryWrapper.eq(DeviceAssembly::getDeviceCode, deviceCode).eq(DeviceAssembly::getDelFlag,CommonConstant.DEL_FLAG_0)
                            .eq(DeviceAssembly::getCode, oldSparePartCode).last("limit 1");
                    DeviceAssembly deviceAssembly = deviceAssemblyService.getBaseMapper().selectOne(assemblyLambdaQueryWrapper);
                    if (Objects.nonNull(deviceAssembly)) {
                        deleteAssemblyList.add(deviceAssembly);
                    }
                }
            });
            sparePartList.stream().forEach(deviceChangeDTO -> {
                // 线处理
                String faultCode = deviceChangeDTO.getCode();
                // 备件更换记录表
                String outOrderId = null;
                if(ObjectUtil.isNotEmpty(deviceChangeDTO.getBorrowingOutOrderId())){
                    outOrderId = deviceChangeDTO.getBorrowingOutOrderId();
                }else {
                    outOrderId = deviceChangeDTO.getLendOutOrderId();
                }
                // 需要往spare_part_malfunction备件履历表
                String createBy = deviceChangeDTO.getCreateBy();
                SysUser loginUser = userMap.get(createBy);
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

        // 保存数据
        if (CollectionUtil.isNotEmpty(malfunctionList)) {
            sparePartMalfunctionService.saveBatch(malfunctionList);
        }
        // 保存数据
        if (CollectionUtil.isNotEmpty(replaceList)) {
            partReplaceService.saveBatch(replaceList);
        }

        // 删除数据
        if (CollectionUtil.isNotEmpty(deleteAssemblyList)) {
            deviceAssemblyService.removeBatchByIds(deleteAssemblyList);
        }
        // 更新数据
        if (CollectionUtil.isNotEmpty(deviceChangeSpareParts)) {
            sparePartService.updateBatchById(deviceChangeSpareParts);
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
            SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPARE_PART_EXTRA);
            String value = sysParamModel.getValue();
            //判断是否删除备件更换记录,只看组件
            if("1".equals(value)){
                deleteDeviceSpare(dtoList,faultCode);
            }
            //旧数据进行判断，是否有移除的,有的话找到出来，然后恢复之前的
            if (CollUtil.isNotEmpty(exitFaultSparePartList)) {
                LambdaQueryWrapper<DeviceChangeSparePart> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(DeviceChangeSparePart::getCode, faultCode);
                if("1".equals(value)){
                    queryWrapper.eq(DeviceChangeSparePart::getConsumables,1);
                }
                List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(queryWrapper);
                List<String> deviceFaultIdList = exitFaultSparePartList.stream().map(SparePartStockDTO::getId).collect(Collectors.toList());
                deviceChangeSparePartList = deviceChangeSparePartList.stream().filter(d ->!deviceFaultIdList.contains(d.getId())).collect(Collectors.toList());
                recoverSparePart(deviceChangeSparePartList);
            }
            //新数据进行判断，是否是自己的仓库
            if (CollUtil.isNotEmpty(unExitFaultSparePartList)) {
                if (CollUtil.isEmpty(exitFaultSparePartList)){
                //旧数据进行判断，是否有移除的,有的话找到出来，然后恢复之前的
                    LambdaQueryWrapper<DeviceChangeSparePart> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(DeviceChangeSparePart::getCode, faultCode);
                    if("1".equals(value)){
                        queryWrapper.eq(DeviceChangeSparePart::getConsumables,1);
                    }
                    List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(queryWrapper);
                    recoverSparePart(deviceChangeSparePartList);
                }
                for (SparePartStockDTO lendStockDTO : unExitFaultSparePartList) {
                    //判断新组件的数量是否大于备件库存
                    SparePartStock sparePartStock = sparePartStockService.getOne(new LambdaQueryWrapper<SparePartStock>()
                            .eq(SparePartStock::getDelFlag, CommonConstant.DEL_FLAG_0)
                            .eq(SparePartStock::getWarehouseCode, lendStockDTO.getWarehouseCode())
                            .eq(SparePartStock::getMaterialCode, lendStockDTO.getMaterialCode()));
                    if(lendStockDTO.getNewSparePartNum()>sparePartStock.getNum()){
                        throw new AiurtBootException("更换的数量大于库存数量！");
                    }
                    DeviceChangeSparePart sparePart = new DeviceChangeSparePart();
                    sparePart.setCode(faultCode);
                    //原组件数量默认1
                    sparePart.setOldSparePartNum(1);
                    sparePart.setNewOrgCode(user.getOrgCode());
                    sparePart.setOldSparePartCode(lendStockDTO.getOldSparePartCode());
                    sparePart.setMaterialBaseCode(lendStockDTO.getMaterialCode());
                    sparePart.setConsumables(lendStockDTO.getConsumablesType());
                    sparePart.setWarehouseCode(lendStockDTO.getWarehouseCode());
                    if ("0".equals(lendStockDTO.getConsumablesType())) {
                        if("1".equals(value)){
                            //先获取该备件的数量记录,更新全新数量
                            LambdaQueryWrapper<SparePartStockNum> numLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            numLambdaQueryWrapper.eq(SparePartStockNum::getMaterialCode, lendStockDTO.getMaterialCode())
                                    .eq(SparePartStockNum::getWarehouseCode, lendStockDTO.getWarehouseCode())
                                    .eq(SparePartStockNum::getDelFlag, CommonConstant.DEL_FLAG_0);
                            SparePartStockNum stockNum = sparePartStockNumMapper.selectOne(numLambdaQueryWrapper);
                            if (stockNum != null) {
                                Integer newNum = stockNum.getNewNum();
                                //如果全新数量小于新组件数量，则从已使用数量中扣除
                                if (newNum < lendStockDTO.getNewSparePartNum()) {
                                    stockNum.setNewNum(0);
                                    stockNum.setUsedNum(stockNum.getUsedNum() - (lendStockDTO.getNewSparePartNum() - newNum));
                                } else {
                                    stockNum.setNewNum(newNum - lendStockDTO.getNewSparePartNum());
                                }
                            }
                            sparePartStockNumMapper.updateById(stockNum);
                        }
                        SparePartScrap scrap = new SparePartScrap();
                        scrap.setStatus(1);
                        scrap.setSysOrgCode(user.getOrgCode());
                        scrap.setMaterialCode(lendStockDTO.getOldMaterialCode());
                        scrap.setWarehouseCode(stockInfo.getWarehouseCode());
                        scrap.setNum(1);
                        scrap.setFaultCode(faultCode);
                        scrap.setScrapTime(new Date());
                        scrap.setCreateBy(user.getUsername());
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
                        QueryWrapper<DeviceAssembly> queryWrapper = new QueryWrapper();
                        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                        List<DeviceAssembly> deviceAssemblyList = deviceAssemblyService.list(queryWrapper);
                        Set<String> assemblyCodeSet = deviceAssemblyList.stream().map(DeviceAssembly::getCode).collect(Collectors.toSet());
                        List<String> strings = new ArrayList<>();
                        Integer newSparePartNum = lendStockDTO.getNewSparePartNum();
                        Integer num = 1;
                        for (Integer i = 0; i < newSparePartNum; i++) {
                            String format = "";
                             do {
                                 String number = String.format("%04d", num);
                                 format = lendStockDTO.getMaterialCode() + number;
                                 num = num + 1;
                            } while (assemblyCodeSet.contains(format));
                            strings.add(format);
                        }
                        String codes = strings.stream().collect(Collectors.joining(","));
                        sparePart.setDeviceCode(lendStockDTO.getDeviceCode());
                         sparePart.setNewSparePartSplitCode(codes);
                         sparePart.setNewSparePartCode(codes);
                    }
                    sparePart.setNewSparePartNum(lendStockDTO.getNewSparePartNum());
                    sparePart.setNewOrgCode(user.getOrgCode());
                    //自己仓库：生成出库单（已确认）
                    if (lendStockDTO.getWarehouseCode().equals(stockInfo.getWarehouseCode())) {
                        SparePartOutOrder lendOutOrder = new SparePartOutOrder();
                        lendOutOrder.setNum(lendStockDTO.getNewSparePartNum());
                        lendOutOrder.setWarehouseCode(lendStockDTO.getWarehouseCode());
                        lendOutOrder.setApplyOutTime(new Date());
                        lendOutOrder.setConfirmTime(new Date());
                        lendOutOrder.setConfirmUserId(user.getId());
                        lendOutOrder.setStatus(2);
                        lendOutOrder.setApplyUserId(user.getUsername());
                        lendOutOrder.setMaterialCode(lendStockDTO.getMaterialCode());
                        List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                                .eq(SparePartOutOrder::getStatus,2)
                                .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                                .eq(SparePartOutOrder::getMaterialCode,lendOutOrder.getMaterialCode())
                                .eq(SparePartOutOrder::getWarehouseCode,lendOutOrder.getWarehouseCode()));
                        if(!orderList.isEmpty()){
                            lendOutOrder.setUnused(orderList.get(0).getUnused());
                        }
                        sparePartOutOrderMapper.insert(lendOutOrder);
                        sparePart.setLendOutOrderId(lendOutOrder.getId());

                        //库存扣减
                        // new 自己的仓库扣减
                        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getMaterialCode, lendOutOrder.getMaterialCode()).eq(SparePartStock::getWarehouseCode, lendOutOrder.getWarehouseCode()));
                        lendStock.setNum(lendStock.getNum() - lendOutOrder.getNum());
                        sparePartStockMapper.updateById(lendStock);
                        sparePart.setBorrowingInventoryOrderId(lendStock.getId());
                        //2023-03-30 测试说不发消息
                        //sendOutboundMessages(lendOutOrder,user);
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

                            sendMeessage(user, sparePartLend);
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
                        List<SparePartOutOrder> orderList = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                                .eq(SparePartOutOrder::getStatus,2)
                                .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                                .eq(SparePartOutOrder::getMaterialCode,lendOutOrder.getMaterialCode())
                                .eq(SparePartOutOrder::getWarehouseCode,lendOutOrder.getWarehouseCode()));
                        if(!orderList.isEmpty()){
                            lendOutOrder.setUnused(orderList.get(0).getUnused());
                        }
                        sparePartOutOrderMapper.insert(lendOutOrder);
                        sparePart.setLendOutOrderId(lendOutOrder.getId());

                        //5.借入仓库库存数做加法

                        SparePartStock  borrowingStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>()
                                .eq(SparePartStock::getMaterialCode, lendOutOrder.getMaterialCode())
                                .eq(SparePartStock::getWarehouseCode, stockInfo.getWarehouseCode()));
                        SparePartStock partStock = new SparePartStock();
                        if (null != borrowingStock) {
                            borrowingStock.setNum(borrowingStock.getNum() + sparePartLend.getLendNum());
                            sparePartStockMapper.updateById(borrowingStock);
                            sparePart.setBorrowingInventoryOrderId(borrowingStock.getId());
                        } else {
                            //插入库存
                            partStock.setMaterialCode(lendOutOrder.getMaterialCode());
                            partStock.setNum(lendOutOrder.getNum());
                            partStock.setWarehouseCode(stockInfo.getWarehouseCode());
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
                        //7.生成借入仓库的出库记录（已确认）
                        SparePartOutOrder borrowingOutOrder = new SparePartOutOrder();
                        borrowingOutOrder.setMaterialCode(sparePartInOrder.getMaterialCode());
                        borrowingOutOrder.setWarehouseCode(sparePartInOrder.getWarehouseCode());
                        borrowingOutOrder.setSysOrgCode(user.getOrgCode());
                        borrowingOutOrder.setNum(sparePartInOrder.getNum());
                        borrowingOutOrder.setApplyOutTime(new Date());
                        borrowingOutOrder.setApplyUserId(user.getUsername());
                        borrowingOutOrder.setConfirmTime(new Date());
                        borrowingOutOrder.setConfirmUserId(user.getId());
                        borrowingOutOrder.setStatus(2);
                        List<SparePartOutOrder> outOrders = sparePartOutOrderMapper.selectList(new LambdaQueryWrapper<SparePartOutOrder>()
                                .eq(SparePartOutOrder::getStatus,2)
                                .eq(SparePartOutOrder::getDelFlag, CommonConstant.DEL_FLAG_0)
                                .eq(SparePartOutOrder::getMaterialCode,borrowingOutOrder.getMaterialCode())
                                .eq(SparePartOutOrder::getWarehouseCode,borrowingOutOrder.getWarehouseCode()));
                        if(!outOrders.isEmpty()){
                            borrowingOutOrder.setUnused(outOrders.get(0).getUnused());
                        }
                        sparePartOutOrderMapper.insert(borrowingOutOrder);
                        sparePart.setBorrowingOutOrderId(borrowingOutOrder.getId());
                        //8.借入仓库的库存扣减（已确认）
                        //库存扣减
                        // new 自己的仓库再扣减
                        if(ObjectUtil.isNotEmpty(borrowingStock)){
                            borrowingStock.setNum(borrowingStock.getNum() - borrowingOutOrder.getNum());
                            sparePartStockMapper.updateById(borrowingStock);
                        }else {
                            partStock.setNum(partStock.getNum() - borrowingOutOrder.getNum());
                            sparePartStockMapper.updateById(partStock);
                        }
                        //2023-03-30 测试说不发消息
                        //sendOutboundMessages(borrowingOutOrder,user);
                    }
//                    sparePart.setNewSparePartCode(lendStockDTO.getNewSparePartCode());
                    sparePartService.getBaseMapper().insert(sparePart);
                    if("1".equals(value)){
                        if("0".equals(sparePart.getConsumables())){
                            List<DeviceChangeSparePartDTO> dataList = new ArrayList<>();
                            DeviceChangeSparePartDTO sparePartDTO = new DeviceChangeSparePartDTO();
                            BeanUtils.copyProperties(sparePart,sparePartDTO);
                            dataList.add(sparePartDTO);
                            dealChangeSparePart(dataList);
                        }
                    }
                }
            }
        } else {
            List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(new LambdaQueryWrapper<DeviceChangeSparePart>().eq(DeviceChangeSparePart::getCode, faultCode));
            recoverSparePart(deviceChangeSparePartList);
        }

    }

    private void deleteDeviceSpare(List<SparePartStockDTO> dtoList,String faultCode) {
        List<String> deviceFaultIdList = dtoList.stream().filter(d -> "0".equals(d.getConsumablesType()) && ObjectUtil.isNotEmpty(d.getId())).map(SparePartStockDTO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<DeviceChangeSparePart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceChangeSparePart::getCode, faultCode);
        queryWrapper.eq(DeviceChangeSparePart::getConsumables,0);
        List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.list(queryWrapper);
        if(CollUtil.isNotEmpty(deviceFaultIdList)){
            List<DeviceChangeSparePart> deleteList = deviceChangeSparePartList.stream().filter(d -> !deviceFaultIdList.contains(d.getId())).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(deleteList)){
                sparePartService.removeBatchByIds(deleteList);
            }
        }
    }

    private void sendMeessage(LoginUser user, SparePartLend sparePartLend) {
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
                    //2023-03-30 测试说先去掉判断，不管这个状态
//                    if (borrowingOutOrder.getStatus() == 1) {
                        //删除借入仓库的出库单
                        sparePartOutOrderMapper.deleteById(borrowingOutOrder);
                        //删除入库单，借入仓库一直是不加不减
                        SparePartInOrder sparePartInOrder = sparePartInOrderMapper.selectOne(new LambdaQueryWrapper<SparePartInOrder>().eq(SparePartInOrder::getId, part.getIntOrderId()));
//                        SparePartStock borrowingStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getId, part.getBorrowingInventoryOrderId()));
//                        borrowingStock.setNum(borrowingStock.getNum() - sparePartInOrder.getNum());
//                        sparePartStockMapper.updateById(borrowingStock);
                        sparePartInOrderMapper.deleteById(sparePartInOrder);
                        //借出仓库库存加回,删除借出仓库的出库单、删除借出单
                        SparePartOutOrder lendOutOrder = sparePartOutOrderMapper.selectOne(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getId, part.getLendOutOrderId()));
                        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getId, part.getLendInventoryOrderId()));
                        lendStock.setNum(lendStock.getNum() + lendOutOrder.getNum());
                        sparePartStockMapper.updateById(lendStock);
                        sparePartOutOrderMapper.deleteById(lendOutOrder);
                        SparePartLend sparePartLend = sparePartLendMapper.selectOne(new LambdaQueryWrapper<SparePartLend>().eq(SparePartLend::getId, part.getLendOrderId()));
                        sparePartLendMapper.deleteById(sparePartLend);
                    //}
                } else {
                    //反则出库流程
                    //自己的仓库加回，删除出库单
                    if (StrUtil.isNotBlank(part.getLendOutOrderId()) && StrUtil.isNotBlank(part.getBorrowingInventoryOrderId())) {
                        SparePartOutOrder sparePartOutOrder = sparePartOutOrderMapper.selectOne(new LambdaQueryWrapper<SparePartOutOrder>().eq(SparePartOutOrder::getId, part.getLendOutOrderId()));
                        SparePartStock lendStock = sparePartStockMapper.selectOne(new LambdaQueryWrapper<SparePartStock>().eq(SparePartStock::getId, part.getBorrowingInventoryOrderId()));
                        lendStock.setNum(lendStock.getNum() + sparePartOutOrder.getNum());
                        sparePartStockMapper.updateById(lendStock);
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
            //labdam方式

            isTodoBaseAPI.createTodoTask(todoDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更换组件
     *
     * @param dataList
     */
    @Override
    public void dealChangeSparePartV2(List<DeviceChangeSparePartDTO> dataList) {
        extracted(dataList);
    }

    /**
     * 查询
     *
     * @param orgId
     * @return
     */
    @Override
    public String getWarehouseCode(String orgId) {

        LambdaQueryWrapper<SparePartStockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePartStockInfo::getOrganizationId, orgId).last("limit 1");
        SparePartStockInfo stockInfo = sparePartStockInfoService.getOne(wrapper);
        if (Objects.nonNull(stockInfo)) {
            return stockInfo.getWarehouseCode();
        }
        return null;
    }

    private void extracted(List<DeviceChangeSparePartDTO> dataList) {
        log.info("处理备件更换流程->{}", JSONObject.toJSONString(dataList));
        Set<String> deviceCodeSet = dataList.stream().map(DeviceChangeSparePartDTO::getDeviceCode).collect(Collectors.toSet());
        List<Device> deviceList = deviceService.list(new LambdaQueryWrapper<Device>().in(Device::getCode, deviceCodeSet)
                .eq(Device::getDelFlag, CommonConstant.DEL_FLAG_0));
        Map<String, Device> deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getCode, t -> t, (t1, t2) -> t1));

        List<DeviceAssembly> addList = new ArrayList<>();
        List<DeviceAssembly> updateList = new ArrayList<>();
        dataList.stream().forEach(deviceChangeSparePartDTO -> {
            String deviceCode = deviceChangeSparePartDTO.getDeviceCode();
            String oldSparePartCode = deviceChangeSparePartDTO.getOldSparePartCode();
            Device device = deviceMap.get(deviceCode);
            if (Objects.isNull(device)) {
                return;
            }
            // 备件更换记录表
            String outOrderId = null;
            if(ObjectUtil.isNotEmpty(deviceChangeSparePartDTO.getBorrowingOutOrderId())){
                outOrderId = deviceChangeSparePartDTO.getBorrowingOutOrderId();
            }else {
                outOrderId = deviceChangeSparePartDTO.getLendOutOrderId();
            }
            if (StrUtil.isBlank(outOrderId)) {
                outOrderService.getById(outOrderId);
            }
            // 查询组件
            DeviceAssembly deviceAssembly = deviceAssemblyService.getOne(new LambdaQueryWrapper<DeviceAssembly>().eq(DeviceAssembly::getDeviceCode, deviceCode)
                    .eq(DeviceAssembly::getCode, oldSparePartCode).eq(DeviceAssembly::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .eq(DeviceAssembly::getStatus, 0).last("limit 1"));

            if (Objects.isNull(deviceAssembly)) {
                return;
            }

            DeviceAssembly assembly = BeanUtil.copyProperties(deviceAssembly, DeviceAssembly.class, "id");
            assembly.setCode(deviceChangeSparePartDTO.getNewSparePartCode());
            assembly.setCreateTime(new Date());

            deviceAssembly.setStatus("1");
            deviceAssembly.setDelFlag(1);
            updateList.add(deviceAssembly);
            addList.add(assembly);


            // spare_part_replace备件更换记录表
            SparePartReplace replace = new SparePartReplace();
            replace.setMaintenanceRecord(deviceChangeSparePartDTO.getCode());
            replace.setMaterialsCode(deviceChangeSparePartDTO.getMaterialBaseCode());
            replace.setOutOrderId(outOrderId);
            replace.setDelFlag(CommonConstant.DEL_FLAG_0);
            // 被替换的组件
            replace.setReplaceSubassemblyCode(oldSparePartCode);
            replace.setSubassemblyCode(deviceChangeSparePartDTO.getNewSparePartCode());
        });

        if (CollUtil.isNotEmpty(addList)) {
            deviceAssemblyService.saveBatch(addList);
        }

        if (CollUtil.isNotEmpty(updateList)) {
            List<String> collect = updateList.stream().map(DeviceAssembly::getId).collect(Collectors.toList());
            deviceAssemblyService.removeBatchByIds(collect);
        }

        List<SparePartMalfunction> sparePartMalfunctionList = dataList.stream().map(deviceChangeDTO -> {
            // 线处理
            String faultCode = deviceChangeDTO.getCode();
            // 备件更换记录表
            String outOrderId = null;
            if (ObjectUtil.isNotEmpty(deviceChangeDTO.getBorrowingOutOrderId())) {
                outOrderId = deviceChangeDTO.getBorrowingOutOrderId();
            } else {
                outOrderId = deviceChangeDTO.getLendOutOrderId();
            }
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
            return sparePartMalfunction;
        }).collect(Collectors.toList());

        // 保存数据
        if (CollectionUtil.isNotEmpty(sparePartMalfunctionList)) {
            sparePartMalfunctionService.saveBatch(sparePartMalfunctionList);
        }
    }
}
