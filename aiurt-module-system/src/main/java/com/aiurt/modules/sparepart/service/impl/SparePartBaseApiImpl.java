package com.aiurt.modules.sparepart.service.impl;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.sparepart.service.*;
import com.google.common.collect.Lists;
import java.util.Date;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.device.service.IDeviceService;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.service.IMaterialBaseService;
import com.aiurt.modules.sparepart.dto.DeviceChangeSparePartDTO;
import com.aiurt.modules.sparepart.dto.SparePartMalfunctionDTO;
import com.aiurt.modules.sparepart.dto.SparePartReplaceDTO;
import com.aiurt.modules.sparepart.dto.SparePartScrapDTO;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.aiurt.modules.sparepart.entity.SparePartReplace;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import liquibase.pro.packaged.I;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISparePartBaseApi;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ISysBaseAPI sysBaseAPI;

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
    private ISparePartOutOrderService outOrderService;

    @Autowired
    private ISparePartScrapService sparePartScrapService;

    @Autowired
    private ISparePartStockInfoService sparePartStockInfoService;

    /**
     * 更新出库单未使用的数量
     * @param updateMap
     */
    @Override
    public void updateSparePartOutOrder(Map<String, Integer> updateMap) {
        if (Objects.nonNull(updateMap) && updateMap.size()>0) {
            updateMap.forEach((id, num)->{
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

            sparePartList.stream().forEach(deviceChange->{

                // 线处理
                String faultCode = deviceChange.getCode();
                // 备件更换记录表
                String outOrderId = deviceChange.getOutOrderId();

                if (StrUtil.isBlank(outOrderId)) {
                    outOrderService.getById(outOrderId);
                }

                String deviceCode = deviceChange.getDeviceCode();
                LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Device::getCode, deviceCode).last("limit 1");
                Device device = deviceService.getBaseMapper().selectOne(queryWrapper);
                if (Objects.isNull(device)) {
                    return;
                }
                // 原组件
                String oldSparePartCode = deviceChange.getOldSparePartCode();

                String newSparePartCode = deviceChange.getNewSparePartCode();

                LambdaQueryWrapper<MaterialBase> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MaterialBase::getCode, newSparePartCode).last("limit 1");
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
                        num = num +1;
                    }while (assemblyCodeSet.contains(key));
                    assemblyCodeSet.add(key);
                    // 组件
                   DeviceAssembly deviceAssembly = new DeviceAssembly();
                   deviceAssembly.setBaseTypeCode(materialBase.getBaseTypeCode());
                   deviceAssembly.setStatus("0");
                   deviceAssembly.setCode(key);
                   deviceAssembly.setManufactorCode(materialBase.getManufactorCode());
                   deviceAssembly.setDeviceCode(deviceCode);
                   deviceAssembly.setMaterialName(materialBase.getName());
                   deviceAssembly.setMaterialCode(key);
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
                    LambdaUpdateWrapper<DeviceAssembly> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(DeviceAssembly::getDeviceCode, deviceCode).eq(DeviceAssembly::getCode, oldSparePartCode)
                            .set(DeviceAssembly::getStatus, "1").set(DeviceAssembly::getDelFlag, CommonConstant.DEL_FLAG_1);
                    deviceAssemblyService.update(updateWrapper);

                    LambdaQueryWrapper<DeviceAssembly> assemblyLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    assemblyLambdaQueryWrapper.eq(DeviceAssembly::getDeviceCode, deviceCode)
                            .eq(DeviceAssembly::getCode, oldSparePartCode).last("limit 1");
                    DeviceAssembly deviceAssembly = deviceAssemblyService.getBaseMapper().selectOne(assemblyLambdaQueryWrapper);

                    // 查询当前替换人员的仓库.
                    SparePartStockInfo stockInfo = sparePartStockInfoService.getSparePartStockInfoByUserName(deviceChange.getCreateBy());
                    if (Objects.nonNull(deviceAssembly)) {
                        // 备件报废表spare_part_scrap插入数据
                        SparePartScrap sparePartScrap = new SparePartScrap();
                        sparePartScrap.setNumber("1");
                        sparePartScrap.setMaterialCode(deviceAssembly.getMaterialCode());
                        sparePartScrap.setWarehouseCode(Objects.isNull(stockInfo)?"":stockInfo.getWarehouseCode());
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

            sparePartList.stream().forEach(deviceChangeDTO->{
                // 线处理
                String faultCode = deviceChangeDTO.getCode();
                // 备件更换记录表
                String outOrderId = deviceChangeDTO.getOutOrderId();
                // 需要往spare_part_malfunction备件履历表
                String createBy = deviceChangeDTO.getCreateBy();
                LoginUser loginUser = sysBaseAPI.getUserByName(createBy);
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


}
