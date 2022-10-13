package com.aiurt.modules.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
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
        //List<SparePartScrapDTO> sparePartScrapList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(sparePartList)) {

            // 插入到组件表中
            sparePartList.stream().forEach(deviceChange->{
                // 原组件
                String oldSparePartCode = deviceChange.getOldSparePartCode();



                if (StrUtil.isNotBlank(oldSparePartCode)) {

                }
            });

            sparePartList.stream().forEach(deviceChangeDTO->{
                // 线处理
                String faultCode = deviceChangeDTO.getCode();
                // 备件更换记录表
                String outOrderId = deviceChangeDTO.getOutOrderId();
                SparePartReplace replace = new SparePartReplace();
                replace.setMaintenanceRecord(faultCode);
                replace.setMaterialsCode(deviceChangeDTO.getNewSparePartCode());
                replace.setOutOrderId(outOrderId);
                replace.setDelFlag(CommonConstant.DEL_FLAG_0);
                // 被替换的组件
                replace.setReplaceSubassemblyCode(deviceChangeDTO.getOldSparePartCode());
                replace.setSubassemblyCode(deviceChangeDTO.getNewSparePartCode());
                list.add(replace);

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
