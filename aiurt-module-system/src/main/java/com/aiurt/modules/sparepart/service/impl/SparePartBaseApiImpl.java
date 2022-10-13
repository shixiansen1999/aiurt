package com.aiurt.modules.sparepart.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.device.service.IDeviceAssemblyService;
import com.aiurt.modules.sparepart.dto.DeviceChangeSparePartDTO;
import com.aiurt.modules.sparepart.dto.SparePartMalfunctionDTO;
import com.aiurt.modules.sparepart.dto.SparePartReplaceDTO;
import com.aiurt.modules.sparepart.dto.SparePartScrapDTO;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.aiurt.modules.sparepart.entity.SparePartReplace;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.service.ISparePartMalfunctionService;
import com.aiurt.modules.sparepart.service.ISparePartReplaceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    public static void main(String[] args) {
        System.out.println(String.format("%012d", 1));
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
        //List<SparePartScrapDTO> sparePartScrapList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(sparePartList)) {

            List<DeviceAssembly> deviceAssemblyList = deviceAssemblyService.list();
            Set<String> assemblyCodeSet = deviceAssemblyList.stream().map(DeviceAssembly::getCode).collect(Collectors.toSet());
            // 插入到组件表中
            sparePartList.stream().forEach(deviceChange->{

                // 线处理
                String faultCode = deviceChange.getCode();
                // 备件更换记录表
                String outOrderId = deviceChange.getOutOrderId();

                String deviceCode = deviceChange.getDeviceCode();

                // 原组件
                String oldSparePartCode = deviceChange.getOldSparePartCode();

                String newSparePartCode = deviceChange.getNewSparePartCode();

                Integer newSparePartNum = Optional.ofNullable(deviceChange.getNewSparePartNum()).orElse(1);

                for (int i = 0; i < newSparePartNum; i++) {
                    String key = "";
                   do {
                       int num = 0;
                       String number = String.format("%03d", num);
                       key = newSparePartCode + number;
                   }while (!assemblyCodeSet.contains(key));

                   DeviceAssembly deviceAssembly = new DeviceAssembly();
                   deviceAssembly.setBaseTypeCode("");
                   deviceAssembly.setBaseTypeCodeName("");
                   deviceAssembly.setStatus("");
                   deviceAssembly.setStatusName("");
                   deviceAssembly.setCode("");
                   deviceAssembly.setManufactorCode("");
                   deviceAssembly.setDeviceCode("");
                   deviceAssembly.setMaterialName("");
                   deviceAssembly.setMaterialCode("");
                   deviceAssembly.setRemark("");
                   deviceAssembly.setSpecifications("");
                   deviceAssembly.setDelFlag(0);
                   deviceAssembly.setCreateBy("");
                   deviceAssembly.setUpdateBy("");
                   deviceAssembly.setCreateTime(new Date());
                   deviceAssembly.setUpdateTime(new Date());
                   deviceAssembly.setStartDate(new Date());
                   deviceAssembly.setPath("");
                   deviceAssembly.setPrice("");
                   deviceAssembly.setDeviceTypeCode("");
                   deviceAssembly.setBuyDate(new Date());
                   deviceAssembly.setOnlineDate(new Date());
                   deviceAssembly.setUnit("");




                    SparePartReplace replace = new SparePartReplace();
                    replace.setMaintenanceRecord(faultCode);
                    replace.setMaterialsCode(newSparePartCode);
                    replace.setOutOrderId(outOrderId);
                    replace.setDelFlag(CommonConstant.DEL_FLAG_0);
                    // 被替换的组件
                    replace.setReplaceSubassemblyCode(oldSparePartCode);
                    replace.setSubassemblyCode("");
                    list.add(replace);
                }

                if (StrUtil.isNotBlank(oldSparePartCode)) {

                }
            });

            sparePartList.stream().forEach(deviceChangeDTO->{
                // 线处理
                String faultCode = deviceChangeDTO.getCode();
                // 备件更换记录表
                String outOrderId = deviceChangeDTO.getOutOrderId();
                // 备件故障记录表
                //
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
    }


}
